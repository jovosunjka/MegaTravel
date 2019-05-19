package com.bsep_sbz.SIEMCenter.unit_test;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import org.drools.core.ClockType;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;
import java.util.*;
import java.util.concurrent.TimeUnit;
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
        assertEquals(1, numOfFiredRules); // mradovic: 1 or 3 ???
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(username, alarm.getLogs().get(0).getSource());
        assertEquals("Neuspesni pokusaji prijave sa istim username-om 2+", alarm.getMessage());
    }

    @Test
    public void testThirtyPlusLoginAttemptsWithinTwentyFourHoursWithSameIP() {
        // Arrange
        KieSession kieSession = getKieSessionWithPseudoClock();
        kieSession.setGlobal("maliciousIpAddresses", new ArrayList<>());

        String ip = "123.2.2.2";
        List<Log> logs = getLoginLogsWithSameSource(30, ip);
        logs.forEach(kieSession::insert);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(30, numOfFiredRules); // mradovic: 30 or 1 ???
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(30, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(ip, alarm.getLogs().get(0).getSource());
        assertEquals("30+ login attempts within 24h with same ip", alarm.getMessage());
        List<String> maliciousIps = (List<String>)kieSession.getGlobal("maliciousIpAddresses");
        assertEquals(30, maliciousIps.size());
        assertEquals(ip, maliciousIps.get(0));
    }

    @Test
    public void testThirtyPlusLoginAttemptsNotWithinTwentyFourHoursWithSameIP() {
        // Arrange
        KieSession kieSession = getKieSessionWithPseudoClock();
        kieSession.setGlobal("maliciousIpAddresses", new ArrayList<>());

        String ip = "123.2.2.2";
        List<Log> logsWithin24h = getLoginLogsWithSameSource(28, ip);
        logsWithin24h.forEach(kieSession::insert);
        SessionPseudoClock clock = kieSession.getSessionClock();
        clock.advanceTime(25, TimeUnit.HOURS);
        List<Log> logsNotWithin24h = getLoginLogsWithSameSource(5, ip);
        logsNotWithin24h.forEach(kieSession::insert);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(0, results.size());
        List<String> maliciousIps = (List<String>)kieSession.getGlobal("maliciousIpAddresses");
        assertEquals(0, maliciousIps.size());
    }

    @Test
    public void testPaymentSystemAttack() {
        // Arrange
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
        KieSession kieSession = kc.newKieSession(ksconf);

        String ip = "133.2.5.6";
        String host = "12.44.33.22";
        List<Log> logs = getPaymentSystemLogs(50, ip, host);
        logs.forEach(kieSession::insert);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(50, numOfFiredRules); // mradovic: 30 or 1 ???
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(50, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(ip, alarm.getLogs().get(0).getSource());
        assertEquals(host, alarm.getLogs().get(0).getHostAddress());
        assertEquals("Payment system attack", alarm.getMessage());
    }

    @Test
    public void testPaymentSystemAttackWhenThereIsNotEnoughRequestsForAlarm() {
        // Arrange
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
        KieSession kieSession = kc.newKieSession(ksconf);

        String ip = "133.2.5.6";
        String host = "12.44.33.22";
        List<Log> logs = getPaymentSystemLogs(45, ip, host);
        logs.forEach(kieSession::insert);
        SessionPseudoClock clock = kieSession.getSessionClock();
        clock.advanceTime(63, TimeUnit.SECONDS);
        List<Log> logsAfterSomeTime = getPaymentSystemLogs(7, ip, host);
        logsAfterSomeTime.forEach(kieSession::insert);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(0, results.size());
    }

    private KieSession getKieSessionWithPseudoClock() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kc = ks.getKieClasspathContainer();
        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        ksconf.setOption(ClockTypeOption.get(ClockType.PSEUDO_CLOCK.getId()));
        return kc.newKieSession(ksconf);
    }

    private List<Log> getPaymentSystemLogs(int count, String source, String host) {
        List<Log> logs = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Log log = new Log();
            log.setId(i + 1L);
            log.setSource(source);
            log.setHostAddress(host);
            log.setCategory(LogCategory.PAYMENT_SYSTEM);
            logs.add(log);
        }
        return logs;
    }

    private List<Log> getLoginLogsWithSameSource(int count, String source) {
        List<Log> logs = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Log log = new Log();
            log.setId(i + 1L);
            log.setType(LogLevel.WARN);
            log.setSource(source);
            log.setMessage("login_successful:false");
            log.setCategory(LogCategory.LOGIN);
            logs.add(log);
        }
        return logs;
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
