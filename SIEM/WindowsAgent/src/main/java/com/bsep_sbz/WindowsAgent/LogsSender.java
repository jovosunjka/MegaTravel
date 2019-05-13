package com.bsep_sbz.WindowsAgent;


import com.bsep_sbz.WindowsAgent.config.AgentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogsSender {
    /*
    //@Value("${scan-logs.monitoring-elements}")
    private List<MonitoringElement> monitoringElements;

    @Value("${scan-logs.includes}")
    private List<String> inludesRegex;

    @Value("${scan-logs.excludes}")
    private List<String> excludesRegex;

    @Value("${scan-logs.interval}")
    private int interval;
*/
    private HashMap<File, Long> startingPositions = new HashMap<File, Long>();

    @Autowired
    private AgentConfiguration agentConfiguration;

    @EventListener(ApplicationReadyEvent.class)
    public void scanLogs() {
        for(AgentConfiguration.MonitoringElement mEelement : agentConfiguration.getMonitoringElements()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runScanLogs(mEelement);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void runScanLogs(AgentConfiguration.MonitoringElement monitoringElement) throws Exception {
        Path path = Paths.get(monitoringElement.getPath());

        if(!Files.isDirectory(path)) {
            if(monitoringElement.getIncludesFiles() != null || monitoringElement.getExcludesFiles() != null) {
                throw new Exception("Ako je u monitoring elementu zadata putanja do fajla, onda ne smemo imati zadate includesFiles"
                        + " i excludesFiles property-je. Popravi to!");
            }
        }
        else {
            if(monitoringElement.getIncludesFiles() == null || monitoringElement.getExcludesFiles() == null) {
                throw new Exception("Ako je u monitoring elementu zadata putanja do direktorijuma, onda moramo imati zadate includesFiles"
                        + " i excludesFiles property-je, pa makar oni bili prazne liste ('[]'). Popravi to!");
            }

        }

        if(agentConfiguration.getInterval() > 0) {
            batchScanLogs(path, monitoringElement.getIncludesFiles(), monitoringElement.getExcludesFiles(),
                    monitoringElement.getIncludes(), monitoringElement.getExcludes());
        }
        else {
            realtimeScanLogs(path, monitoringElement.getIncludesFiles(), monitoringElement.getExcludesFiles(),
                    monitoringElement.getIncludes(), monitoringElement.getExcludes());
        }
    }

    public void batchScanLogs(Path path, List<String> includesFiles,  List<String> excludesFiles,
                              List<String> includes,  List<String> excludes) {

        while (true) {
            if(!Files.isDirectory(path)) {
                readLogsAndUpdateStartPosition(path.toFile(), includes, excludes);
            }
            else {
                File[] filteredFiles = filterFilesInDirectory(path.toFile(), includesFiles, excludesFiles);

                for (File file : filteredFiles) {
                    readLogsAndUpdateStartPosition(file, includes, excludes);
                }
            }


            try {
                Thread.sleep(agentConfiguration.getInterval()*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void realtimeScanLogs(Path path, List<String> includesFiles,  List<String> excludesFiles,
                                 List<String> includes,  List<String> excludes) {
        System.out.println("realtimeScanLogs (folder/file="+path.toFile().getAbsolutePath()+")(START)");
        //String directoryPath = "E:"+ File.separator +"STUDIRANJE"+ File.separator +"CETVRTA GODINA";

        Path directoryPath;
        Path singleFilePath;
        if(!Files.isDirectory(path)) {
            singleFilePath = path;
            directoryPath = path.getParent();
        }
        else {
            singleFilePath = null;
            directoryPath = path;
        }

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();


            directoryPath.register(
                    watchService,
                    //StandardWatchEventKinds.ENTRY_CREATE,
                    //StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            WatchKey key;
            Path filePath;
            Path fullFilePath;

            while (true) {
                key = watchService.take(); // take je blokirajuca metoda
                if(key == null) continue;

                /*for (WatchEvent<?> event : key.pollEvents()) {
                    filePath = (Path) event.context();
                    //System.out.println("Event kind:" + event.kind() + ". File affected: " + fileName + ". Count: " + event.count() + ".");
                    fullFilePath = directoryPath.resolve(filePath);
                    if(singleFilePath == null || (singleFilePath != null && singleFilePath.equals(fullFilePath))) {
                        readLogsAndUpdateStartPosition(fullFilePath, includes, excludes);
                    }
                }*/

                List<File> files = key.pollEvents().stream()
                        .map(event -> directoryPath.resolve((Path) event.context()).toFile())
                        .collect(Collectors.toList());

                if (singleFilePath != null) {
                    files.stream()
                            .filter(file -> file.equals(singleFilePath.toFile()))
                            .forEach(file -> readLogsAndUpdateStartPosition(file, includes, excludes));
                }
                else {
                    files.stream()
                            .filter(file -> (includesFiles.isEmpty()|| matchesRegexes(file.getName(), includesFiles))
                                    && (excludesFiles.isEmpty() || !matchesRegexes(file.getName(), excludesFiles)))
                            .forEach(file -> readLogsAndUpdateStartPosition(file, includes, excludes));
                }

                key.reset();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("realtimeScanLogs (END)");
    }

    public void readLogsAndUpdateStartPosition(File file, List<String> includes, List<String> excludes) {
        long startPos;

        if(startingPositions.containsKey(file)) {
            startPos = startingPositions.get(file);
        }
        else {
            startPos = 0;
            //startPos = filePath.toFile().length();
        }
        startPos = readLogs(file, startPos, includes, excludes);
        startingPositions.put(file, startPos);
    }

    public long readLogs(File file, long startingPosition, List<String> includes, List<String> excludes) {
        long lengthRead;
        byte[] bytesRead;

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = file.length();

            if (fileLength == 0) {
                startingPosition = 0;
                System.out.println("File " + file.getAbsolutePath() + " is empty!");
                return startingPosition;
            }
            else if (startingPosition > fileLength) {
                startingPosition = fileLength;
                System.out.println("Something in the file " + file.getAbsolutePath() + " has been deleted. Check it out!");
                return startingPosition;
            }
            lengthRead = fileLength - startingPosition;

            randomAccessFile.seek(startingPosition);
            bytesRead = new byte[(int) lengthRead];
            randomAccessFile.read(bytesRead);
            startingPosition += lengthRead;
            randomAccessFile.close();

            String newLogsStr = new String(bytesRead);
            newLogsStr = newLogsStr.trim();
            String[] newLogs = newLogsStr.split("\r\n");
            List<String> filteredNewLogs = filterLogs(newLogs, includes, excludes);

            for (String fNewLog : filteredNewLogs) {
                System.out.println("Filtered new log in file " + file.getAbsolutePath() + ": " + fNewLog);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return startingPosition;
    }

    private File[] filterFilesInDirectory(File directory, List<String> includesFiles, List<String> excludesFiles) {
        File[] filteredFiles = directory.listFiles(
                (File dir, String name) -> (includesFiles.isEmpty() || matchesRegexes(name, includesFiles))
                        && (excludesFiles.isEmpty() || !matchesRegexes(name, excludesFiles))
        );
        // Koristimo lambda izraze za implementaciju Funkcionalnog interfejsa (interfejs koji ima samo jednu metodu)
        // umesto standardne implementacije

        /*File[] filteredFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (includesFiles.isEmpty() || matchesRegexes(name, includesFiles))
                        && (excludesFiles.isEmpty() || !matchesRegexes(name, excludesFiles));
            }
        });*/

        return filteredFiles;
    }

    private List<String> filterLogs(String[] newLogs, List<String> includes, List<String> excludes) {
        List<String> filteredLogs = Arrays.stream(newLogs)
                .filter(newLog -> !newLog.equals("")
                        && (includes.isEmpty() || matchesRegexes(newLog, includes)) // lokalni regexi
                        && (excludes.isEmpty() || !matchesRegexes(newLog, excludes)) // lokalni regexi
                        && (agentConfiguration.getIncludes().isEmpty() || matchesRegexes(newLog, agentConfiguration.getIncludes())) // globalni regexi
                        && (agentConfiguration.getExcludes().isEmpty() || !matchesRegexes(newLog, agentConfiguration.getExcludes())) // globalni regexi
                )
                .collect(Collectors.toList());

        return  filteredLogs;
    }

    public boolean matchesRegexes(String str, List<String> regexes) {
        for (String regex : regexes) {
            if(str.matches(regex)) return true;
        }

        return false;
    }

}

