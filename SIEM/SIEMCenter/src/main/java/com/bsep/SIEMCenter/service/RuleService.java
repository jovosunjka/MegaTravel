package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.service.interfaces.IRuleService;
import com.bsep.SiemCenterRules.app.DebugAgendaEventListener;
import com.bsep.SiemCenterRules.app.KnowledgeSessionHelper;
import com.bsep.SiemCenterRules.model.AntivirusLog;
import com.bsep.SiemCenterRules.model.LoginLog;
import com.bsep.SiemCenterRules.model.UserAccount;
import com.bsep.SiemCenterRules.model.enums.AccountType;
import com.bsep.SiemCenterRules.model.enums.HostType;
import com.bsep.SiemCenterRules.model.enums.LogLevel;
import com.bsep.SiemCenterRules.model.enums.RiskLevel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RuleService implements IRuleService {

    @Autowired
    private KieContainer kieContainer;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void initialize() {
        System.out.println("************* INITIALIZE ****************");
        KieSession kSession = KnowledgeSessionHelper.getStatefulKnowledgeSession(kieContainer, "SbzRulesSession");

        kSession.addEventListener(new DebugAgendaEventListener());

        UserAccount userAccount = new UserAccount("test", LocalDateTime.now().minusDays(10),
                RiskLevel.LOW, AccountType.SIEMOPERATER);
        LoginLog loginLog1 = new LoginLog(new Long(1L), LogLevel.ERROR, LocalDateTime.now(), "hostAddress1",
                HostType.App, userAccount, "ipAddress1", false);
        LoginLog loginLog2 = new LoginLog(new Long(2L), LogLevel.ERROR, LocalDateTime.now().plusMinutes(5), "hostAddress1",
                HostType.App, userAccount, "ipAddress1", false);
        LoginLog loginLog3 = new LoginLog(new Long(3L), LogLevel.ERROR, LocalDateTime.now().plusMinutes(10), "hostAddress1",
                HostType.App, userAccount, "ipAddress1", false);

        LoginLog loginLog4 = new LoginLog(new Long(4L), LogLevel.ERROR, LocalDateTime.now().minusDays(90), "hostAddress4",
                HostType.App, userAccount, "ipAddress4", false);

        AntivirusLog antivirusLog1 = new AntivirusLog(5L, LogLevel.ERROR, LocalDateTime.now(), "hostAddress100", null);
        AntivirusLog antivirusLog2 = new AntivirusLog(6L, LogLevel.INFO, LocalDateTime.now().plusHours(2), "hostAddress100", antivirusLog1);

        kSession.insert(loginLog1);
        kSession.insert(loginLog2);
        kSession.insert(loginLog3);
        kSession.insert(loginLog4);
        kSession.insert(antivirusLog1);
        kSession.insert(antivirusLog2);

        int fired = kSession.fireAllRules();
        System.out.println("fired: " + fired);

        kSession.dispose();
    }
}
