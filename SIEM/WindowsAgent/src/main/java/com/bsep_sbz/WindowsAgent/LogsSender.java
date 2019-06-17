package com.bsep_sbz.WindowsAgent;


import com.bsep_sbz.WindowsAgent.config.AgentConfiguration;
import com.bsep_sbz.WindowsAgent.model.ReadLogsResult;
import com.bsep_sbz.WindowsAgent.service.interfaces.ILogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

// Ako ovde ne stoji anotacija @Component, onda anotacija @EventListener(ApplicationReadyEvent.class) iznad metode scanLogs,
// nece imati nikakvog efekta
@Component
public class LogsSender {

    private HashMap<File, Long> startingPositions = new HashMap<File, Long>();

    @Autowired
    private AgentConfiguration agentConfiguration;

    @Autowired
    private ILogsService logsService;

    private Path windowsLogsDirectory = Paths.get("C:\\Windows\\System32\\winevt\\Logs");


    @EventListener(ApplicationReadyEvent.class)
    private void scanLogs() {
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

    private void runScanLogs(AgentConfiguration.MonitoringElement monitoringElement) throws Exception {
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

        int interval;
        if (monitoringElement.getInterval() != null) {
            interval = monitoringElement.getInterval();
        }
        else if (agentConfiguration.getInterval() != null) {
            interval = agentConfiguration.getInterval();
        }
        else {
            throw new Exception("Za monitoring element sa path-om: " + monitoringElement.getPath() + ", interval je null," +
                    " a uz to je globalni interval null. Popravite to! Bar jedan od ta dva intervala (lokalni interval za" +
                    " ovaj monitoring element ili globalni interval) mora biti razlicit od null i nenegativan broj.");
        }

        if(interval > 0) {
            System.out.println("batchScanLogs (folder/file="+path.toFile().getAbsolutePath()+", interval="+interval+")(START)");
            batchScanLogs(path, interval, monitoringElement.getIncludesFiles(), monitoringElement.getExcludesFiles(),
                    monitoringElement.getIncludes(), monitoringElement.getExcludes());
        }
        else {
            System.out.println("realtimeScanLogs (folder/file="+path.toFile().getAbsolutePath()+")(START)");
            realtimeScanLogs(path, monitoringElement.getIncludesFiles(), monitoringElement.getExcludesFiles(),
                    monitoringElement.getIncludes(), monitoringElement.getExcludes());
        }
    }

    private void batchScanLogs(Path path, int interval, List<String> includesFiles,  List<String> excludesFiles,
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
                Thread.sleep(interval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void realtimeScanLogs(Path path, List<String> includesFiles,  List<String> excludesFiles,
                                 List<String> includes,  List<String> excludes) {
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

    private void readLogsAndUpdateStartPosition(File file, List<String> includes, List<String> excludes) {
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

    private long readLogs(File file, long startingPosition, List<String> includes, List<String> excludes) {
        ReadLogsResult rlr;

        if(file.toPath().startsWith(windowsLogsDirectory)) {
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length()-5); // .evtx je ekstenzija za windows lgove,
                                                                    // skinucemo ovih 5 karaktera
            rlr = readWindowsLogs(fileName, startingPosition);
            // fileName bice dovoljan, jer powerShell koji koristimo, zna gde da trazi windows logove
            // za readWindowsLogs, startingPosition predstavlja broj karaktera koje cemo preskociti
        }
        else {
            rlr = readPlainLogs(file, startingPosition);
        }

        List<String> filteredNewLogs = filterLogs(rlr.getLogs(), includes, excludes);

        for (String fNewLog : filteredNewLogs) {
            System.out.println("Filtered new log in file " + file.getAbsolutePath() + ": " + fNewLog);
        }

        try {
            logsService.sendLogs(filteredNewLogs);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return rlr.getNumberOfCharactersOrBytesRead();
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

    private List<String> filterLogs(List<String> newLogs, List<String> includes, List<String> excludes) {
        List<String> filteredLogs = newLogs.stream()
                .filter(newLog -> !newLog.equals("")
                        && (includes.isEmpty() || matchesRegexes(newLog, includes)) // lokalni regexi
                        && (excludes.isEmpty() || !matchesRegexes(newLog, excludes)) // lokalni regexi
                        && (agentConfiguration.getIncludes().isEmpty() || matchesRegexes(newLog, agentConfiguration.getIncludes())) // globalni regexi
                        && (agentConfiguration.getExcludes().isEmpty() || !matchesRegexes(newLog, agentConfiguration.getExcludes())) // globalni regexi
                )
                .collect(Collectors.toList());

        return  filteredLogs;
    }

    private boolean matchesRegexes(String str, List<String> regexes) {
        for (String regex : regexes) {
            if(str.matches(regex)) return true;
        }

        return false;
    }

    private ReadLogsResult readWindowsLogs(String logName, long skipCharacters) {
        System.out.println("READ WINDOWS LOGS (logName="+logName+", skipCharacters="+skipCharacters+")");
        //https://docs.microsoft.com/en-us/powershell/module/microsoft.powershell.management/get-eventlog?view=powershell-5.1

        // Get-EventLog -LogName Security -Newest 5 | Select-Object -Property *
        //  Get-EventLog -LogName Security | Format-Table TimeCreated,Message -wrap
        String command = "powershell.exe  Get-EventLog -LogName " + logName + " | Select-Object -Property *";

        ArrayList<String> logs = new ArrayList<String>();

        try {
            // Executing the command
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            // Getting the results
            powerShellProcess.getOutputStream().close();
            //System.out.println("Standard Output:");
            InputStreamReader isr = new InputStreamReader(powerShellProcess.getInputStream());
            if(skipCharacters > 0) isr.skip(skipCharacters);
            BufferedReader br = new BufferedReader(isr);

            String line;
            String newLog;
            StringBuilder logBuilder = new StringBuilder("");

            boolean removeLine = false;
            while ((line = br.readLine()) != null) {
                skipCharacters += line.length();

                if (line.matches("Source\\s*:.*")) {
                    removeLine = false;
                }
                if (removeLine) continue;

                line = line.trim();
                if (line.equals("")) continue;


                //line.split("\\s*:\\s*"); // nula ili vise razmaka sa obe strane dvotacke
                logBuilder.append("|");
                logBuilder.append(line);

                if (line.matches("Message\\s*:.*")) {
                    newLog = logBuilder.substring(1);
                    newLog = newLog.replaceAll("\\s*:\\s*", "=");
                    logs.add(newLog);
                    logBuilder = new StringBuilder("");
                    removeLine = true;
                }
            }
            br.close();

            /*System.out.println("Standard Error:");
            BufferedReader stderr = new BufferedReader(new InputStreamReader(powerShellProcess.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.out.println(line);
            }
            stderr.close();*/
            //System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ReadLogsResult(logs, skipCharacters);
    }

    private ReadLogsResult readPlainLogs(File file, long startingPosition) {
        long lengthRead;
        byte[] bytesRead;

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = file.length();

            if (fileLength == 0) {
                startingPosition = 0;
                System.out.println("File " + file.getAbsolutePath() + " is empty!");
                return new ReadLogsResult(startingPosition);
            }
            else if (startingPosition > fileLength) {
                startingPosition = fileLength;
                System.out.println("Something in the file " + file.getAbsolutePath() + " has been deleted. Check it out!");
                return  new ReadLogsResult(startingPosition);
            }
            lengthRead = fileLength - startingPosition;

            randomAccessFile.seek(startingPosition);
            bytesRead = new byte[(int) lengthRead];
            randomAccessFile.read(bytesRead);
            startingPosition += lengthRead;
            randomAccessFile.close();

            String newLogsStr = new String(bytesRead);
            newLogsStr = newLogsStr.trim();
            String[] newLogs = newLogsStr.split("\r?\n");

            return new ReadLogsResult(Arrays.asList(newLogs), startingPosition);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}

