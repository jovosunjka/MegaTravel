package com.bsep_sbz.WindowsAgent;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogsSender {
    @Value("${scan-logs.paths}")
    private List<String> pathsStr;

    @Value("${scan-logs.includes}")
    private List<String> inludesRegex;

    @Value("${scan-logs.excludes}")
    private List<String> excludesRegex;

    @Value("${scan-logs.interval}")
    private int interval;

    private HashMap<Path, Long> startingPositions = new HashMap<Path, Long>();


    @EventListener(ApplicationReadyEvent.class)
    public void scanLogs() {
        for(String pathStr : pathsStr) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Path path = Paths.get(pathStr);
                    if(interval > 0) {
                        batchScanLogs(path);
                    }
                    else {
                        realtimeScanLogs(path);
                    }
                }
            }).start();
        }
    }

    public void batchScanLogs(Path path) {
        while (true) {
            readLogsAndUpdateStartPosition(path);
            try {
                Thread.sleep(interval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void realtimeScanLogs(Path path) {
        System.out.println("realtimeScanLogs (folder/file="+path.getName(path.getNameCount()-1)+")(START)");
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

                for (WatchEvent<?> event : key.pollEvents()) {
                    filePath = (Path) event.context();
                    //System.out.println("Event kind:" + event.kind() + ". File affected: " + fileName + ". Count: " + event.count() + ".");
                    fullFilePath = directoryPath.resolve(filePath);
                    if(singleFilePath == null || (singleFilePath != null && singleFilePath.equals(fullFilePath))) {
                        readLogsAndUpdateStartPosition(fullFilePath);
                    }
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

    public void readLogsAndUpdateStartPosition(Path filePath) {
        long startPos;

        if(startingPositions.containsKey(filePath)) {
            startPos = startingPositions.get(filePath);
        }
        else {
            startPos = 0;
            //startPos = filePath.toFile().length();
        }
        startPos = readLogs(filePath, startPos);
        startingPositions.put(filePath, startPos);
    }

    public long readLogs(Path path, long startingPosition) {
        long lengthRead;
        byte[] bytesRead;

        try {
            RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
            long fileLength = file.length();

            if (fileLength == 0) {
                startingPosition = 0;
                System.out.println("File " + path.getName(path.getNameCount()-1) + " is empty!");
                return startingPosition;
            }
            else if (startingPosition > fileLength) {
                startingPosition = fileLength;
                System.out.println("Something in the file " + path.getName(path.getNameCount()-1) + " has been deleted. Check it out!");
                return startingPosition;
            }
            lengthRead = fileLength - startingPosition;

            file.seek(startingPosition);
            bytesRead = new byte[(int) lengthRead];
            file.read(bytesRead);
            startingPosition += lengthRead;
            file.close();

            String newLogsStr = new String(bytesRead);
            newLogsStr = newLogsStr.trim();
            String[] newLogs = newLogsStr.split("\r\n");
            List<String> filteredNewLogs = Arrays.stream(newLogs)
                    .filter(newLog -> !newLog.equals("")
                            && matchesRegexes(newLog, inludesRegex)
                            && !matchesRegexes(newLog, excludesRegex)
                    )
                    .collect(Collectors.toList());
            for (String fNewLog : filteredNewLogs) {
                System.out.println("Filtered new log in file " + path.getName(path.getNameCount()-1) + ": " + fNewLog);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return startingPosition;
    }

    public boolean matchesRegexes(String log, List<String> regexes) {
        for (String regex : regexes) {
            if(log.matches(regex)) return true;
        }

        return false;
    }

}

