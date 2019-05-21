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
*/

import java.time.format.DateTimeFormatter;


public class App {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss");

    public static void main(String[] args) {
/*
        KieContainer kc = KnowledgeSessionHelper.createRuleBase();
        KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kc, "SbzRulesSession");

        kSession.addEventListener(new DebugAgendaEventListener());


        Log loginLog1 = null;
        Log loginLog2 = null;
        Log loginLog3 = null;
        Log loginLog4 = null;
        Log antivirusLog1 = null;
        Log antivirusLog2 = null;
        try {
            loginLog1 = new Log(new Long(1L), LogLevel.WARN, LogCategory.LOGIN, dtf.format(LocalDateTime.now()),
                    "ipAddress1", "hostAddress1", "login_successful:false,username:jovo");
            loginLog2 = new Log(new Long(2L), LogLevel.WARN, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusMinutes(5)),
                    "ipAddress2", "hostAddress2", "login_successful:false,username:jovo");
            loginLog3 = new Log(new Long(3L), LogLevel.WARN, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusMinutes(10)),
                    "ipAddress3", "hostAddress3", "login_successful:false");
            loginLog4 = new Log(new Long(4L), LogLevel.WARN, LogCategory.LOGIN, dtf.format(LocalDateTime.now().minusDays(90)),
                    "ipAddress4", "hostAddress4", "login_successful:false");

            antivirusLog1 = new Log(new Long(5L), LogLevel.WARN, LogCategory.ANTIVIRUS, dtf.format(LocalDateTime.now()),
                    "ipAddress100", "hostAddress100", "message1");
            antivirusLog2 = new Log(new Long(6L), LogLevel.INFO, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusHours(2)),
                    "ipAddress100", "hostAddress100", "solved_log:5");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        kSession.insert(loginLog1);
        kSession.insert(loginLog2);
        kSession.insert(loginLog3);
        kSession.insert(loginLog4);
        kSession.insert(antivirusLog1);
        kSession.insert(antivirusLog2);

        try {
            for(int i = 0; i < 14; i++) {
                kSession.insert(new Log(new Long(1000+i), LogLevel.WARN, LogCategory.LOGIN , dtf.format(LocalDateTime.now()), "ipAddress", "hostAddress", "login_successful:false"));
            }
            kSession.insert(new Log(new Long(999), LogLevel.WARN, LogCategory.LOGIN , dtf.format(LocalDateTime.now().minusDays(5).plusMinutes(1)), "ipAddress", "hostAddress", "login_successful:false"));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String[] ipAddresses = {"ipAddress1", "ipAddress2", "ipAddress3", "ipAddress4"};
        String[] hostAddresses = {"hostAddress1", "hostAddress1", "hostAddress2", "hostAddress3"};

        try {
            for(int i = 0; i < 4; i++) {
                kSession.insert(new Log(new Long(300+i), LogLevel.INFO, LogCategory.LOGIN , dtf.format(LocalDateTime.now().minusSeconds(i)), ipAddresses[i], hostAddresses[i], "login_successful:true,username:aaa"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            for(int i = 0; i < 7; i++) {
                kSession.insert(new Log(new Long(400+i), LogLevel.ERROR, LogCategory.ANTIVIRUS , dtf.format(LocalDateTime.now().minusDays(i)), "", "hostAddress", ""));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] usernames = {"aaa", "bbb", "ccc", "ddd", "eee"};
        try {
            for(int i = 0; i < 5; i++) {
                kSession.insert(new Log(new Long(500+i), LogLevel.WARN, LogCategory.LOGIN , dtf.format(LocalDateTime.now().minusSeconds((i+1)*10)), "ipAddress", "", "login_successful:false,username:"+usernames[i]));
            }
            kSession.insert(new Log(new Long(506), LogLevel.INFO, LogCategory.LOGIN , dtf.format(LocalDateTime.now()), "ipAddress", "", "login_successful:true"));
            kSession.insert(new Log(new Long(507), LogLevel.INFO, LogCategory.APP , dtf.format(LocalDateTime.now()), "ipAddress", "", "user_data_updated_successful:true"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int fired = kSession.fireAllRules();
        System.out.println("fired: " + fired);

*/
        // Markanovo

        //testLogs();
        //testLogin();
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
