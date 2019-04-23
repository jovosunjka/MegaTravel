package com.bsep.SiemCenterRules.app;

import com.bsep.SiemCenterRules.model.LoginLog;
import com.bsep.SiemCenterRules.model.enums.LogLevel;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class App {

    public static void main(String[] args) {
        testLogs();
        //testLogin();
    }

    private static KieSession getKieSession(String sessionName) {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer.newKieSession(sessionName);
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

        LoginLog loginLog = new LoginLog();
        loginLog.setTimestamp(LocalDateTime.of(LocalDate.of(2016, 12, 12),
                LocalTime.of(0, 0, 0)));
        kSession.insert(loginLog);
        kSession.fireAllRules();
    }
}
