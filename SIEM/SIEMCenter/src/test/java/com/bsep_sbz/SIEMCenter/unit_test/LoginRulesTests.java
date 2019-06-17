package com.bsep_sbz.SIEMCenter.unit_test;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import org.apache.tomcat.jni.Local;
import org.drools.core.ClockType;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.time.SessionPseudoClock;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LoginRulesTests {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss");

    @Test
    public void test_Pojava_loga_u_kojoj_se_nalazi_IP_adresa_sa_spiska_malicioznih_IP_adresa() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

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
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(1, alarm.getLogs().size());
        assertEquals(alarm.getLogs().get(0).getSource(), maliciousIp);
        assertEquals("Pojava loga u kojoj se nalazi IP adresa sa spiska malicioznih IP adresa", alarm.getMessage());
    }


    @Test
    public void testLoginWithMaliciousIpAddress() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        ArrayList<String> maliciousIps = new ArrayList<>();
        maliciousIps.add("127.0.1.0");
        String maliciousIp = "125.6.7.8";
        maliciousIps.add(maliciousIp);
        kieSession.setGlobal("maliciousIpAddresses", maliciousIps);

        Log log = new Log();
        log.setType(LogLevel.WARN);
        log.setCategory(LogCategory.LOGIN);
        log.setMessage("login_successful:false");
        log.setSource(maliciousIp);
        kieSession.insert(log);

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(2, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(2, results.size());
        Iterator<QueryResultsRow> iter = results.iterator();

        Alarm alarm1 = (Alarm) iter.next().get("$a");
        assertEquals(1, alarm1.getLogs().size());
        assertEquals(alarm1.getLogs().get(0).getSource(), maliciousIp);
        assertEquals("Try to login from malicious ip address", alarm1.getMessage());

        Alarm alarm2 = (Alarm) iter.next().get("$a");
        assertEquals(1, alarm2.getLogs().size());
        assertEquals(alarm2.getLogs().get(0).getSource(), maliciousIp);
        assertEquals("Pojava loga u kojoj se nalazi IP adresa sa spiska malicioznih IP adresa", alarm2.getMessage());
    }

    @Test
    public void testLoginAttemptAfterNinetyDaysInactivity(){
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        String username = "user123";
        Date currentLoginAttemptDate = new GregorianCalendar(2019, Calendar.MAY, 1).getTime();
        Log currentLoginLog = new Log();
        currentLoginLog.setCategory(LogCategory.LOGIN);
        currentLoginLog.setTimestamp(currentLoginAttemptDate);
        currentLoginLog.setMessage(String.format("Login attempt with username '%s' from ip address '195.57.27.32'", username));
        kieSession.insert(currentLoginLog);

        Date previousLoginDate = new GregorianCalendar(2019, Calendar.JANUARY, 20).getTime();
        Log previousLoginLog = new Log();
        previousLoginLog.setCategory(LogCategory.LOGIN);
        previousLoginLog.setTimestamp(previousLoginDate);
        previousLoginLog.setMessage(String.format("Login attempt with username '%s' from ip address '195.57.27.32'", username));
        kieSession.insert(previousLoginLog);
        assertTrue(Log.getDaysOfInactivity(currentLoginAttemptDate.getTime(),
                                                                                previousLoginDate.getTime()) >= 90);

        // Act
        kieSession.getAgenda().getAgendaGroup("app-long").setFocus();
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
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

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
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        /*
        assertEquals(1, alarm.getLogs().size());
        assertEquals(hostAddress, alarm.getLogs().get(0).getHostAddress());
        */
        assertEquals("Neuspesni pokusaji prijave na sistem na istoj masini 2+", alarm.getMessage());
    }

    @Test
    public void testUnsuccessfulLoginsWithSameUsername() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        Log log1 = new Log();
        log1.setId(1L);
        log1.setType(LogLevel.WARN);
        log1.setCategory(LogCategory.LOGIN);
        log1.setHostAddress("12.22.42.11");
        log1.setMessage("Login attempt with username 'luser123' from ip address '195.57.27.32'");
        kieSession.insert(log1);

        Log log2 = new Log();
        log1.setId(2L);
        log2.setType(LogLevel.WARN);
        log2.setCategory(LogCategory.LOGIN);
        log2.setHostAddress("72.25.32.31");
        log2.setMessage("Login attempt with username 'luser123' from ip address '195.57.27.32'");
        kieSession.insert(log2);

        Log log3 = new Log();
        log1.setId(3L);
        log3.setType(LogLevel.WARN);
        log3.setCategory(LogCategory.LOGIN);
        log3.setHostAddress("32.32.43.31");
        log3.setMessage("Login attempt with username 'luser123' from ip address '195.57.27.32'");
        kieSession.insert(log3);

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        /*
        assertEquals(1, alarm.getLogs().size());
        assertEquals(username, alarm.getLogs().get(0).getSource());
        */
        assertEquals("Neuspesni pokusaji prijave sa istim username-om 2+", alarm.getMessage());
    }

    @Test
    public void testThirtyPlusLoginAttemptsWithinTwentyFourHoursWithSameIP() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");
        kieSession.setGlobal("maliciousIpAddresses", new ArrayList<>());

        String ip = "123.2.2.2";
        List<Log> logs = getLoginLogsWithSameSource(30, ip);
        logs.forEach(kieSession::insert);

        // Act
        kieSession.getAgenda().getAgendaGroup("app-long").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        Alarm alarm = null;
        for(QueryResultsRow r : results) {
            Alarm a = (Alarm)r.get("$a");
            if(a.getMessage().equals("Ukoliko sa iste IP adrese registruje 30 ili više neuspešnih pokušaja prijave")) {
                alarm = a;
                break;
            }
        }
        assertNotNull(alarm);
        assertEquals(1, alarm.getLogs().size());
        assertEquals(ip, alarm.getLogs().get(0).getSource());
        List<String> maliciousIps = (List<String>)kieSession.getGlobal("maliciousIpAddresses");
        assertEquals(1, maliciousIps.size());
        assertEquals(ip, maliciousIps.get(0));
    }

    @Test
    public void testThirtyPlusLoginAttemptsNotWithinTwentyFourHoursWithSameIP() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");
        kieSession.setGlobal("maliciousIpAddresses", new ArrayList<>());

        String ip = "123.2.2.2";
        List<Log> logsWithin24h = getLoginLogsWithSameSource(28, ip);
        logsWithin24h.forEach(x -> x.setTimestamp(new Date(dtf.format(LocalDateTime.now()))));
        logsWithin24h.forEach(kieSession::insert);
        List<Log> logsNotWithin24h = getLoginLogsWithSameSource(5, ip);
        logsNotWithin24h.forEach(x -> x.setTimestamp(new Date(dtf.format(LocalDateTime.now().minusHours(25)))));
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
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        String ip = "133.2.5.6";
        String host = "12.44.33.22";
        List<Log> logs = getPaymentSystemLogs(50, ip, host);
        logs.forEach(kieSession::insert);

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals("Payment system attack", alarm.getMessage());
    }

    @Test
    public void testPaymentSystemAttackWhenThereIsNotEnoughRequestsForAlarm() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        String ip = "133.2.5.6";
        String host = "12.44.33.22";
        List<Log> logs = getPaymentSystemLogs(45, ip, host, dtf.format(LocalDateTime.now()));
        logs.forEach(kieSession::insert);
        List<Log> logsAfterSomeTime = getPaymentSystemLogs(7, ip, host, dtf.format(LocalDateTime.now().minusSeconds(65)));
        logsAfterSomeTime.forEach(kieSession::insert);

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(0, results.size());
    }

    @Test
    public void test_Neuspesni_pokusaji_prijave_15plus_u_roku_od_5_dana() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");
        String source = "ipAddress";
        List<Log> logs = new ArrayList<>();
        try {
            for(int i = 0; i < 25; i++) {
                Log log = new Log(new Long(1000+i), LogLevel.WARN, LogCategory.LOGIN ,
                        dtf.format(LocalDateTime.now().minusMinutes(i)), source,
                        "hostAddress"+i, "");
                logs.add(log);
                kieSession.insert(log);
            }
            Log log = new Log(new Long(999), LogLevel.WARN, LogCategory.LOGIN ,
                    dtf.format(LocalDateTime.now().minusDays(5).plusMinutes(1)),
                    source, "hostAddress", "");
            logs.add(log);
            kieSession.insert(log);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("app-long").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        //assertEquals(1, alarm.getLogs().size());
        assertEquals("15 ili više neuspešnih pokušaja prijave na različite delove informacionog sistema", alarm.getMessage());
    }

    @Test
    public void test_Prijavljivanje_na_sistem_u_razmaku_manjem_od_10_sekundi_sa_razlicitih_IP_adresa() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        String[] ipAddresses = {"ipAddress1", "ipAddress2", "ipAddress3", "ipAddress4"};
        String[] hostAddresses = {"hostAddress1", "hostAddress1", "hostAddress2", "hostAddress3"};

        try {
            for(int i = 0; i < 4; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.INFO, LogCategory.LOGIN ,
                        dtf.format(LocalDateTime.now().minusSeconds(i)), ipAddresses[i], hostAddresses[i],
                        "Login attempt with username 'luser123' from ip address '195.57.27.32'"));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        //assertEquals(1, alarm.getLogs().size());
        assertEquals("Prijavljivanje na sistem u razmaku manjem od 10 sekundi sa razlicitih IP adresa", alarm.getMessage());
    }

    @Test
    public void test_U_periodu_od_10_dana_registrovano_7_ili_vise_pretnji_od_strane_antivirusa_za_isti_racunar() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");
        List<Log> logs = new ArrayList<>();
        try {
            for(int i = 0; i < 14; i++){
                Log log = new Log(new Long(i), LogLevel.WARN, LogCategory.ANTIVIRUS , dtf.format(LocalDateTime.now().minusDays(i)),
                        "", "hostAddress", "");
                logs.add(log);
                kieSession.insert(log);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("antivirus-long").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        //assertEquals(1, alarm.getLogs().size());
        assertEquals("U periodu od 10 dana registrovano 7 ili vise pretnji od strane antivirusa za isti racunar", alarm.getMessage());
    }

    @Test
    public void test_Non_Activate_U_periodu_od_10_dana_registrovano_7_ili_vise_pretnji_od_strane_antivirusa_za_isti_racunar() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        try {
            for(int i = 0; i < 6; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.ERROR, LogCategory.ANTIVIRUS , dtf.format(LocalDateTime.now().minusDays(i)),
                        "", "hostAddress", ""));
            }

            for(int i = 0; i < 3; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.ERROR, LogCategory.ANTIVIRUS , dtf.format(LocalDateTime.now().minusDays((i+1)*11)),
                        "", "hostAddress", ""));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(0, results.size());
    }

    @Test
    public void test_Uspesna_prijava_na_sistem_pracena_sa_izmenom_korisnickih_podataka_ukoliko_je_sa_iste_IP_adrese() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        String[] usernames = {"aaa", "bbb", "ccc", "ddd", "eee"};
        try {
            for(int i = 0; i < 5; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.WARN, LogCategory.LOGIN ,
                        dtf.format(LocalDateTime.now().minusSeconds((i+1)*10)),
                        "ipAddress", "hostAddress"+i,
                        String.format("Login attempt with username '%s' from ip address '195.57.27.32'", usernames[i])));
            }
            kieSession.insert(new Log(new Long(6), LogLevel.INFO, LogCategory.LOGIN ,
                    dtf.format(LocalDateTime.now()), "ipAddress", "",
                    "login_successful:true"));
            kieSession.insert(new Log(new Long(7), LogLevel.INFO, LogCategory.APP ,
                    dtf.format(LocalDateTime.now()), "ipAddress", "",
                    "user_data_updated_successful:true"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        //assertEquals(1, alarm.getLogs().size());
        assertEquals("Uspesna prijava na sistem pracena sa izmenom korisnickih podataka ukoliko je sa iste IP adrese", alarm.getMessage());
    }

    @Test
    public void test_Pojava_loga_u_kome_antivirus_registruje_pretnju_a_da_u_roku_od_1h_se_ne_generise_log_o_uspesnom_eliminisanju_pretnje() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        try {
            Log antivirusLog1 = new Log(new Long(5L), LogLevel.WARN, LogCategory.ANTIVIRUS, dtf.format(LocalDateTime.now()),
                    "ipAddress100", "hostAddress100", "message1");
            Log antivirusLog2 = new Log(new Long(6L), LogLevel.INFO, LogCategory.ANTIVIRUS, dtf.format(LocalDateTime.now().plusHours(2)),
                    "ipAddress100", "hostAddress100", "solvedLogId:5");
            kieSession.insert(antivirusLog1);
            kieSession.insert(antivirusLog2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("antivirus").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        assertEquals(2, alarm.getLogs().size());
        assertEquals("Pojava loga u kome antivirus registruje pretnju, a da u roku od 1h se ne generise log o uspesnom eliminisanju pretnje", alarm.getMessage());
    }

    @Test
    public void test_Activate_Zahtevi_bilo_kog_tipa_aktiviraju_alarm_za_DoS_napad() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        try {
            for(int i = 0; i < 51; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.INFO, LogCategory.APP,
                        dtf.format(LocalDateTime.now()),
                        "ipAddress"+i, "hostAddress"+i, "message"+i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        //assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        for(QueryResultsRow r : results) {
            Alarm a = (Alarm)r.get("$a");
            System.out.println(a.getMessage());
        }
        //assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
       // assertEquals(2, alarm.getLogs().size());
        assertEquals("Zahtevi bilo kog tipa aktiviraju alarm za DoS napad", alarm.getMessage());
    }

    @Test
    public void test_Non_Activate_Zahtevi_bilo_kog_tipa_aktiviraju_alarm_za_DoS_napad() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        LogCategory[] enums = LogCategory.values();
        LogLevel[] typeEnums = LogLevel.values();
        try {
            for(int i = 0; i < 50; i++) {
                kieSession.insert(new Log(new Long(i), typeEnums[i % typeEnums.length], enums[i % enums.length], dtf.format(LocalDateTime.now()),
                        "ipAddress"+i, "hostAddress"+i, "message"+i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
    }

    @Test
    public void test_Activate_Zahtev_koji_su_povezani_sa_prijavom_korisnika_aktiviraju_brute_force_alarm() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        try {
            for(int i = 0; i < 51; i++) {
                kieSession.insert(new Log(new Long(i), LogLevel.INFO, LogCategory.APP,
                        dtf.format(LocalDateTime.now().minusSeconds(i)),
                        "ipAddress"+i, "hostAddress"+i, "message"+i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        kieSession.getAgenda().getAgendaGroup("app").setFocus();
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(1, numOfFiredRules);
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        assertEquals(1, results.size());
        Alarm alarm = (Alarm) results.iterator().next().get("$a");
        // assertEquals(2, alarm.getLogs().size());
        assertEquals("Zahtevi bilo kog tipa aktiviraju alarm za DoS napad", alarm.getMessage());
    }

    @Test
    public void test_Non_Activate_Zahtev_koji_su_povezani_sa_prijavom_korisnika_aktiviraju_brute_force_alarm() {
        // Arrange
        KieSession kieSession = KnowledgeSessionHelper.getKieSession("logs-session");

        LogLevel[] typeEnums = LogLevel.values();
        try {
            for(int i = 0; i < 50; i++) {
                kieSession.insert(new Log(new Long(i), typeEnums[i % typeEnums.length], LogCategory.LOGIN, dtf.format(LocalDateTime.now()),
                        "ipAddress"+i, "hostAddress"+i, "message"+i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Act
        int numOfFiredRules = kieSession.fireAllRules();

        // Assert
        assertEquals(0, numOfFiredRules);
    }

    private List<Log> getPaymentSystemLogs(int count, String source, String host, String timestampStr) {
        List<Log> logs = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Log log = null;
            try {
                log = new Log(i +1L, LogLevel.WARN, LogCategory.PAYMENT_SYSTEM, timestampStr, source, host, "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            logs.add(log);
        }
        return logs;
    }

    private List<Log> getPaymentSystemLogs(int count, String source, String host) {
        List<Log> logs = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            Log log = null;
            try {
                log = new Log(i +1L, LogLevel.WARN, LogCategory.PAYMENT_SYSTEM,
                        dtf.format(LocalDateTime.now()), source, host, "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
            log.setCategory(LogCategory.LOGIN);
            log.setTimestamp(new Date(dtf.format(LocalDateTime.now().minusSeconds(i+1))));
            logs.add(log);
        }
        return logs;
    }
}
