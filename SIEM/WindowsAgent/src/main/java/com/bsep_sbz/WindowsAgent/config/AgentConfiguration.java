package com.bsep_sbz.WindowsAgent.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;


//https://github.com/konrad-garus/so-yaml/blob/master/src/main/java/io/example/AvailableChannelsConfiguration.java


// Dodali smo i dependency spring-boot-configuration-processor
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "scan-logs")
//@Configuration
public class AgentConfiguration {
    private List<MonitoringElement> monitoringElements;
    private List<String> includes; // Globalni regexi za filtriranje sadrzaja u fajlovima
    private List<String> excludes; // Globalni regexi za filtriranje sadrzaja u fajlovima
    private Integer interval; // seconds
    // globalni interval
    // stavlili smo Integer, a ne int, da bi interval imao vrednost null, a ne 0, ako ne bude definisan


    public AgentConfiguration() {

    }

    public AgentConfiguration(List<MonitoringElement> monitoringElements, List<String> includes, List<String> excludes, Integer interval) {
        this.monitoringElements = monitoringElements;
        this.includes = includes;
        this.excludes = excludes;
        this.interval = interval;
    }

    public List<MonitoringElement> getMonitoringElements() {
        return monitoringElements;
    }

    public void setMonitoringElements(List<MonitoringElement> monitoringElements) {
        this.monitoringElements = monitoringElements;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }


    @Component
    @ConfigurationProperties(prefix = "scan-logs.monitoring-elements")
    //@Configuration
    public static class MonitoringElement {
        private String path;
        private List<String> includesFiles; // ako je data putanja do foldera, onda se ovo moze koristiti (Regexi za filtriranje fajlova)
        private List<String> excludesFiles; // ako je data putanja do foldera, onda se ovo moze koristiti (Regexi za filtriranje fajlova)
        private List<String> includes; // Regexi za filtriranje sadrzaja u fajlovima
        private List<String> excludes; // Regexi za filtriranje sadrzaja u fajlovima
        private Integer interval; // seconds
        // lokalni interval
        // stavlili smo Integer, a ne int, da bi interval imao vrednost null, a ne 0, ako ne bude definisan

        public MonitoringElement() {

        }

        public MonitoringElement(String path, List<String> includesFiles, List<String> excludesFiles, List<String> includes, List<String> excludes, Integer interval) {
            this.path = path;
            this.includesFiles = includesFiles;
            this.excludesFiles = excludesFiles;
            this.includes = includes;
            this.excludes = excludes;
            this.interval = interval;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public List<String> getIncludesFiles() {
            return includesFiles;
        }

        public void setIncludesFiles(List<String> includesFiles) {
            this.includesFiles = includesFiles;
        }

        public List<String> getExcludesFiles() {
            return excludesFiles;
        }

        public void setExcludesFiles(List<String> excludesFiles) {
            this.excludesFiles = excludesFiles;
        }

        public List<String> getIncludes() {
            return includes;
        }

        public void setIncludes(List<String> includes) {
            this.includes = includes;
        }

        public List<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(List<String> excludes) {
            this.excludes = excludes;
        }

        public Integer getInterval() { return interval; }

        public void setInterval(Integer interval) { this.interval = interval; }
    }

}
