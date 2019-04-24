package com.bsep.SIEMCenter.service;


import com.bsep.SIEMCenter.model.sbz.Log;
import com.bsep.SIEMCenter.model.sbz.enums.LogCategory;
import com.bsep.SIEMCenter.model.sbz.enums.LogLevel;
import com.bsep.SIEMCenter.service.interfaces.IRuleService;
import com.bsep.SIEMCenter.util.DebugAgendaEventListener;
import com.bsep.SIEMCenter.util.KnowledgeSessionHelper;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class RuleService implements IRuleService {

    @Autowired
    private KieContainer kieContainer;

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
            LoginLog loginLog = new LoginLog();
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
}
