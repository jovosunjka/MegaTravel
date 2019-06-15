package com.bsep_sbz.SIEMCenter.unit_test;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import static org.junit.Assert.assertEquals;

public class SimpleRulesTests {

    @Test
    public void testErrorLogRule() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        Log log = new Log();
        log.setType(LogLevel.ERROR);
        kieSession.insert(log);

        kieSession.getAgenda().getAgendaGroup("app").setFocus();

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(alarm.getLogs().get(0).getType(), LogLevel.ERROR);
        assertEquals("Pojava loga čiji tip je ERROR", alarm.getMessage());
    }

    @Test
    public void testMultipleSessions() {
        // Arrange
        KieSession kieSession1 = KnowledgeSessionHelper.getKieSession("logs-session");
        KieSession kieSession2 = KnowledgeSessionHelper.getKieSession("logs-session");
        KieSession kieSession3 = KnowledgeSessionHelper.getKieSession("logs-session");

        Log log1 = new Log();
        log1.setType(LogLevel.DEBUG);
        kieSession1.insert(log1);

        Log log2 = new Log();
        log2.setType(LogLevel.ERROR);
        kieSession2.insert(log2);

        Log log3 = new Log();
        log3.setType(LogLevel.WARN);
        kieSession3.insert(log3);

        kieSession1.getAgenda().getAgendaGroup("app").setFocus();
        kieSession2.getAgenda().getAgendaGroup("app").setFocus();
        kieSession3.getAgenda().getAgendaGroup("app").setFocus();

        // Act
        int numOfFiredRules1 = kieSession1.fireAllRules();
        int numOfFiredRules2 = kieSession2.fireAllRules();
        int numOfFiredRules3 = kieSession3.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules1);
        assertEquals(1, numOfFiredRules2);
        assertEquals(0, numOfFiredRules3);
    }
}
