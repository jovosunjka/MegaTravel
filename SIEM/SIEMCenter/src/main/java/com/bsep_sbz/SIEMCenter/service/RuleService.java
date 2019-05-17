package com.bsep_sbz.SIEMCenter.service;


import com.bsep_sbz.SIEMCenter.model.sbz.Log;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.LogLevel;
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
import java.util.Date;
import java.util.List;

@Service
public class RuleService implements IRuleService {

    @Autowired
    private KieContainer kieContainer;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

    @EventListener(ApplicationReadyEvent.class)
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

        List<Log> logsRet = new ArrayList<>();
        for (String log: logs) {
            String[] tokens = log.split("\\|");
            //# log id|event id|timestamp|log lvl type|message
            try {
                Long id = Long.parseLong(tokens[0]);
                String timestamp = tokens[2];
                LogLevel ll = LogLevel.valueOf(tokens[3]);
                String message = tokens[4];
                LogCategory logCategory = LogCategory.APP;
                Log logCreated = new Log(id,ll, logCategory,timestamp, "","", message);
                logsRet.add(logCreated);


            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
        return logsRet;
    }
}