package com.bsep_sbz.SIEMCenter.service;


import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import com.bsep_sbz.SIEMCenter.util.DebugAgendaEventListener;
import com.bsep_sbz.SIEMCenter.util.KnowledgeSessionHelper;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RuleService implements IRuleService {

    @Autowired
    private KieContainer kieContainer;

    //private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss");

    //@EventListener(ApplicationReadyEvent.class)
    @Override
    public void initialize() {
        System.out.println("************* INITIALIZE ****************");
        String sessinName;
        sessinName = "SbzRulesSession";
        //sessinName = "logs-session";
        //sessinName = "login-session";
        KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kieContainer, sessinName);

        kSession.addEventListener(new DebugAgendaEventListener());

        Log loginLog1 = null;
        Log loginLog2 = null;
        Log loginLog3 = null;
        Log loginLog4 = null;
        Log antivirusLog1 = null;
        Log antivirusLog2 = null;

        try {
            loginLog1 = new Log(new Long(1L), LogLevel.ERROR, LogCategory.LOGIN, dtf.format(LocalDateTime.now()),
                    "username1", "hostAddress1", "login_successfull:false");
            loginLog2 = new Log(new Long(2L), LogLevel.ERROR, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusMinutes(5)),
                    "username1", "hostAddress2", "login_successfull:false");
            loginLog3 = new Log(new Long(3L), LogLevel.ERROR, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusMinutes(10)),
                    "username1", "hostAddress3", "login_successfull:false");
            loginLog4 = new Log(new Long(4L), LogLevel.ERROR, LogCategory.LOGIN, dtf.format(LocalDateTime.now().minusDays(91)),
                    "username1", "hostAddress4", "login_successfull:false");

            antivirusLog1 = new Log(new Long(5L), LogLevel.ERROR, LogCategory.ANTIVIRUS, dtf.format(LocalDateTime.now()),
                    "hostAddress100", "hostAddress100", "message1");
            antivirusLog2 = new Log(new Long(6L), LogLevel.INFO, LogCategory.LOGIN, dtf.format(LocalDateTime.now().plusHours(2)),
                    "hostAddress100", "hostAddress100", "solved_log:5");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        kSession.insert(loginLog1);
        kSession.insert(loginLog2);
        kSession.insert(loginLog3);
        kSession.insert(loginLog4);
        kSession.insert(antivirusLog1);
        kSession.insert(antivirusLog2);

/*
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
            LoginMessage loginLog = new LoginMessage();
            loginLog.setIpAddress(maliciousIp);
            loginLog.setTimestamp(LocalDateTime.of(LocalDate.of(2016, 12, 12),
                    LocalTime.of(0, 0, 0)));
            kSession.insert(loginLog);
            //kSession.fireAllRules();

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
        int fired = kSession.fireAllRules();
        System.out.println("fired: " + fired);

        kSession.dispose();
    }

    public List<Log> makeLogs(List<String> logs) {
        //# log id|event id|timestamp|log lvl type|message
        List<Log> logsRet = new ArrayList<>();
        for (String log: logs) {
            String[] tokens = log.split("\\|");
            try {
                Long id = Long.parseLong(getValue(tokens,"id"));
                String timestamp = getValue(tokens,"timestamp");
                if(timestamp.endsWith("PM")) {
                    timestamp = timestamp.replace("PM", "").trim();
                }
                if((new Date(timestamp)).after(new Date(dtf.format(LocalDateTime.now())))) {
                    continue;
                }
                String hostAddress = getValue(tokens, "hostaddress");
                LogLevel logLevel = LogLevel.valueOf(getValue(tokens, "loglevel"));
                LogCategory logCategory = LogCategory.valueOf(getValue(tokens, "logcategory"));
                String message = getValue(tokens, "message");
                String source = getSource(message);
                logsRet.add(new Log(id, logLevel, logCategory, new Date(timestamp), source, hostAddress, message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logsRet;
    }

    private String getValue(String[] tokens, String key) {
        return Arrays.stream(tokens).filter(x -> x.startsWith(key)).findFirst().get().split(":", 2)[1].trim();
    }

    private String getSource(String message) {
        if(message.contains("ip address"))
        {
            String[] splittedMessage = message.split("\\s+");
            int i = 0;
            for(; i<splittedMessage.length; i++) {
                if(splittedMessage[i].equals("address")) {
                    break;
                }
            }
            String ipAddress = splittedMessage[++i];
            return ipAddress.replaceAll("'", "");
        }
        else if(message.contains("username"))
        {
            String[] splittedMessage = message.split("\\s+");
            int i = 0;
            for(; i<splittedMessage.length; i++) {
                if(splittedMessage[i].equals("username")) {
                    break;
                }
            }
            String username = splittedMessage[++i];
            return username.replaceAll("'", "");
        }

        return "";
    }
}