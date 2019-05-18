package com.bsep_sbz.SiemCenterRules.app;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
/*
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import java.time.LocalDateTime;
*/


public class App {


    public static void main(String[] args) {
        KieContainer kc = KnowledgeSessionHelper.createRuleBase();
        KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "SbzRulesSession");

        kSession.addEventListener(new DebugAgendaEventListener());

/*
        Log loginLog1 = new Log(new Long(1L), LogLevel.ERROR, LogCategory.LOGIN, LocalDateTime.now(),
                "username1", "hostAddress1", "login_successfull:false");
        Log loginLog2 = new Log(new Long(2L), LogLevel.ERROR, LogCategory.LOGIN, LocalDateTime.now().plusMinutes(5),
                "username1", "hostAddress2", "login_successfull:false");
        Log loginLog3 = new Log(new Long(3L), LogLevel.ERROR, LogCategory.LOGIN, LocalDateTime.now().plusMinutes(10),
                "username1", "hostAddress3", "login_successfull:false");
        Log loginLog4 = new Log(new Long(4L), LogLevel.ERROR, LogCategory.LOGIN, LocalDateTime.now().minusDays(90),
                "username1", "hostAddress4", "login_successfull:false");

        Log antivirusLog1 = new Log(new Long(5L), LogLevel.ERROR, LogCategory.ANTIVIRUS, LocalDateTime.now(),
                "hostAddress100", "hostAddress100", "message1");
        Log antivirusLog2 = new Log(new Long(6L), LogLevel.INFO, LogCategory.LOGIN, LocalDateTime.now().plusHours(2),
                "hostAddress100", "hostAddress100", "solved_log:5");

        kSession.insert(loginLog1);
        kSession.insert(loginLog2);
        kSession.insert(loginLog3);
        kSession.insert(loginLog4);
        kSession.insert(antivirusLog1);
        kSession.insert(antivirusLog2);

        int fired = kSession.fireAllRules();
        System.out.println("fired: " + fired);


        // Markanovo

        //testLogs();
        //testLogin();
        */
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
        KieBase kbase = kc.newKieBase("sbz/rules/loginrules", kbconf);
        return kc.newKieSession(sessionName);
    }

    private static void testLogs() {
        /*
        KieSession kSession = getKieSession("logs-session");
        LoginLog log = new LoginLog();
        log.setLogLevel(LogLevel.ERROR);
        kSession.insert(log);
        kSession.fireAllRules();*/
    }

    private static void testLogin() {
        /*
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
        */
    }
}
