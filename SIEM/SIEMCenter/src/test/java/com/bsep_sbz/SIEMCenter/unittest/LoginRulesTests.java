package com.bsep_sbz.SIEMCenter.unittest;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginRulesTests {

    @Test
    public void testLoginWithMaliciousIpAddress() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("login-session");

        ArrayList<String> maliciousIps = new ArrayList<>();
        maliciousIps.add("127.0.1.0");
        String maliciousIp = "125.6.7.8";
        maliciousIps.add(maliciousIp);
        kieSession.setGlobal("maliciousIpAddresses", maliciousIps);

        Log log = new Log();
        log.setType(LogLevel.INFO);
        log.setSource(maliciousIp);
        kieSession.insert(log);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(alarm.getLogs().get(0).getSource(), maliciousIp);
        assertEquals("Try to login from malicious ip address", alarm.getMessage());
    }

    @Test
    public void testLoginAttemptAfterNinetyDaysInactivity(){
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("SbzRulesSession");

        String username = "user123";
        Date currentLoginAttemptDate = new GregorianCalendar(2019, Calendar.MAY, 1).getTime();
        Log currentLoginLog = new Log();
        currentLoginLog.setCategory(LogCategory.LOGIN);
        currentLoginLog.setSource(username);
        currentLoginLog.setTimestamp(currentLoginAttemptDate);
        kieSession.insert(currentLoginLog);

        Date previousLoginDate = new GregorianCalendar(2019, Calendar.JANUARY, 20).getTime();
        Log previousLoginLog = new Log();
        previousLoginLog.setCategory(LogCategory.LOGIN);
        previousLoginLog.setSource(username);
        previousLoginLog.setTimestamp(previousLoginDate);
        kieSession.insert(previousLoginLog);
        assertTrue(Log.getDaysOfInactivity(currentLoginAttemptDate.getTime(),
                                                                                previousLoginDate.getTime()) >= 90);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(previousLoginLog.getId(), alarm.getLogs().get(0).getId());
        assertEquals("Pokusaj prijave na nalog koji nije bio aktivan 90+ dana", alarm.getMessage());
    }

    @Test
    public void testUnsuccessfulLoginsOnSameHost() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("SbzRulesSession");

        String hostAddress = "12.33.21.12";
        Log log1 = new Log();
        log1.setId(1L);
        log1.setType(LogLevel.WARN);
        log1.setCategory(LogCategory.LOGIN);
        log1.setSource("112.321.132");
        log1.setHostAddress(hostAddress);
        log1.setMessage("login_successful:false");
        kieSession.insert(log1);

        Log log2 = new Log();
        log1.setId(2L);
        log2.setType(LogLevel.WARN);
        log2.setCategory(LogCategory.LOGIN);
        log1.setSource("412.421.142");
        log2.setHostAddress(hostAddress);
        log2.setMessage("login_successful:false");
        kieSession.insert(log2);

        Log log3 = new Log();
        log1.setId(3L);
        log3.setType(LogLevel.WARN);
        log3.setCategory(LogCategory.LOGIN);
        log1.setSource("115.521.152");
        log3.setHostAddress(hostAddress);
        log3.setMessage("login_successful:false");
        kieSession.insert(log3);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(hostAddress, alarm.getLogs().get(0).getHostAddress());
        assertEquals("Neuspesni pokusaji prijave na sistem na istoj masini 2+", alarm.getMessage());
    }

    @Test
    public void testUnsuccessfulLoginsWithSameUsername() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("SbzRulesSession");

        String username = "user123";
        Log log1 = new Log();
        log1.setId(1L);
        log1.setType(LogLevel.WARN);
        log1.setCategory(LogCategory.LOGIN);
        log1.setSource(username);
        log1.setHostAddress("12.22.42.11");
        log1.setMessage("login_successful:false");
        kieSession.insert(log1);

        Log log2 = new Log();
        log1.setId(2L);
        log2.setType(LogLevel.WARN);
        log2.setCategory(LogCategory.LOGIN);
        log2.setSource(username);
        log1.setHostAddress("72.25.32.31");
        log2.setMessage("login_successful:false");
        kieSession.insert(log2);

        Log log3 = new Log();
        log1.setId(3L);
        log3.setType(LogLevel.WARN);
        log3.setCategory(LogCategory.LOGIN);
        log3.setSource(username);
        log1.setHostAddress("32.32.43.31");
        log3.setMessage("login_successful:false");
        kieSession.insert(log3);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(username, alarm.getLogs().get(0).getSource());
        assertEquals("Neuspesni pokusaji prijave sa istim username-om 2+", alarm.getMessage());
    }

    /*
    // test template

    @Test
    public void test() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession();

        kieSession.insert();

        // Act
        int numOfFiredRules = kieSession.fireAllRules();


        // Assert
        assertEquals(1, numOfFiredRules);

    }
    */
}
