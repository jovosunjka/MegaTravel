package com.bsep_sbz.SIEMCenter.unit_test;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleRulesTests {

    @Test
    public void testErrorLogRule() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        Log log = new Log();
        log.setType(LogLevel.ERROR);
        kieSession.insert(log);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(alarm.getLogs().get(0).getType(), LogLevel.ERROR);
        assertEquals("Log with ERROR", alarm.getMessage());
    }

}
