package com.bsep.SiemCenterRules.app;

import com.bsep.SiemCenterRules.model.LoginLog;
import com.bsep.SiemCenterRules.model.enums.LogLevel;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class App {

    public static void main(String[] args) {
        //testLogs();
        testLogin();
    }

    private static KieSession getKieSession(String sessionName) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.newKieSession(sessionName);
    }

    private static KieSession getKieSessionForStream(String sessionName) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
        kbconf.setOption(EventProcessingOption.STREAM);
        KieBase kbase = kc.newKieBase("loginrules", kbconf);
        return kc.newKieSession(sessionName);
    }

    private static void testLogs() {
        KieSession kSession = getKieSession("logs-session");
        LoginLog log = new LoginLog();
        log.setLogLevel(LogLevel.ERROR);
        kSession.insert(log);
        kSession.fireAllRules();
    }

    private static void testLogin() {
        KieSession kSession = getKieSession("login-session");
        ArrayList<String> maliciousIps = new ArrayList<>();
        String maliciousIp = "125.6.7.8";
        maliciousIps.add("127.0.1.0");
        maliciousIps.add(maliciousIp);
        kSession.setGlobal("maliciousIpAddresses", maliciousIps);
        System.out.println("Malicious ip addresses before rules: ");
        for (String i :
                maliciousIps) {
            System.out.println(i);
        }
        try {
            LoginLog loginLog = new LoginLog();
            loginLog.setIpAddress(maliciousIp);
            loginLog.setTimestamp(LocalDateTime.of(LocalDate.of(2016, 12, 12),
                    LocalTime.of(0, 0, 0)));
            kSession.insert(loginLog);
            kSession.fireAllRules();

            System.out.println("Malicious ip addresses after rules: ");
            for (String i :
                    maliciousIps) {
                System.out.println(i);
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
