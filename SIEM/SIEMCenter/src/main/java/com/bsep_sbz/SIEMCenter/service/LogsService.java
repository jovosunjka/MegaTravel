package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.controller.dto.LoginTemplateDto;
import com.bsep_sbz.SIEMCenter.controller.dto.PageableDto;
import com.bsep_sbz.SIEMCenter.helper.Constants;
import com.bsep_sbz.SIEMCenter.helper.HelperMethods;
import com.bsep_sbz.SIEMCenter.helper.ValidationException;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.model.sbz.rule.LoginData;
import com.bsep_sbz.SIEMCenter.repository.AlarmRepository;
import com.bsep_sbz.SIEMCenter.repository.LogsRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import com.bsep_sbz.SIEMCenter.websockets.Producer;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LogsService implements ILogsService
{
    @Autowired
    private Producer producer;
    @Autowired
    private AlarmRepository alarmRepository;
    @Autowired
    private KieContainer kieContainer;
    @Autowired
    private LogsRepository logsRepository;
    private KieSession appKieSession;
    private KieSession appLongKieSession;
    private KieSession antivirusKieSession;
    private KieSession antivirusLongKieSession;
    private final Object appMutex = new Object();
    private final Object appLongMutex = new Object();
    private final Object antivirusMutex = new Object();
    private final Object antivirusLongMutex = new Object();
    private static int loginTemplateCounter = 0;
    private LocalDateTime appLongLastTimeFired = LocalDateTime.now();
    private LocalDateTime antivirusLastTimeFired = LocalDateTime.now();
    private LocalDateTime antivirusLongLastTimeFired = LocalDateTime.now();

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSessions() {
        appKieSession = getKieSession();
        appKieSession.setGlobal("maliciousIpAddresses", new ArrayList<String>());
    }

    private KieSession getKieSession() {
        return kieContainer.newKieSession("logs-session");
    }

    @Override
    public void save(List<Log> logs) {
        logs.forEach(logsRepository::save);
    }

    @Override
    public Page<Log> getFilteredLogs(String column, String regExp, Pageable pageable) {
        if(regExp == null) {
            return null;
        }
        if(regExp.isEmpty()) {
            return null;
        }
        if(pageable.getPageSize() < 1) {
            return null;
        }
        if(pageable.getPageNumber() < 0) {
            return null;
        }

        switch (column) {
            case Constants.LogFields.id:
                return logsRepository.findByIdRegexAndPagination(regExp, pageable);
            case Constants.LogFields.type:
                return logsRepository.findByTypeRegexAndPagination(regExp, pageable);
            case Constants.LogFields.category:
                return logsRepository.findByCategoryRegexAndPagination(regExp, pageable);
            case Constants.LogFields.source:
                return logsRepository.findBySourceRegexAndPagination(regExp, pageable);
            case Constants.LogFields.timestamp:
                return logsRepository.findByTimestampRegexAndPagination(regExp, pageable);
            case Constants.LogFields.host_address:
                return logsRepository.findByHostAddressRegexAndPagination(regExp, pageable);
            case Constants.LogFields.message:
                return logsRepository.findByMessageRegexAndPagination(regExp, pageable);
            default:
                return null;
        }
    }

    @Override
    public void generateRule(LoginTemplateDto dto)
            throws ValidationException, IOException, MavenInvocationException {
        // 0) Validate request
        validateLoginTemplateRequestDto(dto);
        // WARNING !!! Kada se pokrene spring-boot:run working directory je tamo gde se nalazi pom.xml pokrenutog projekta,
        // a ako se pokrene u debug rezimu onda je working directory MegaTravel root folder !!!
        String templatePath = "..\\SiemCenterRules\\src\\main\\resources\\sbz\\" +
                "rules\\templates\\login_attempt.drt";
        String drlPath = "..\\SiemCenterRules\\src\\main\\resources\\sbz\\" +
                "rules\\LoginAttemptRule" + loginTemplateCounter + ".drl";

        // 1) Read template
        InputStream template = new FileInputStream(templatePath);

        // 2) Compile template to drl rule
        LoginData loginData = new LoginData(dto.getLoginAttemptCount(), dto.getTimeCount(), dto.getTimeUnit(),
                dto.getHostRelation(), dto.getSourceRelation(), dto.isLoginSuccess(),
                loginTemplateCounter);
        ++loginTemplateCounter;
        List<LoginData> data = new ArrayList<>();
        data.add(loginData);
        String drl = (new ObjectDataCompiler()).compile(data, template);

        // 3) Save drl rule to file
        Files.write( Paths.get(drlPath), drl.getBytes(), StandardOpenOption.CREATE);

        // 4) maven clean, maven install
        HelperMethods.mavenCleanAndInstallRules();
    }

    @Override
    public void insertInSession(List<Log> logRet) {
        // antivirus logs will be retrieved from db and inserted in session later
        List<Log> nonAntivirusLogs = logRet.stream().filter(x -> x.getCategory() != LogCategory.ANTIVIRUS)
                .collect(Collectors.toList());
        if(!nonAntivirusLogs.isEmpty()) {
            synchronized (appMutex) {
                nonAntivirusLogs.forEach(appKieSession::insert);
            }
        }
    }

    @Scheduled(fixedRate = 2000, initialDelay = 10000)
    public void fireRules() {
        // app agenda-group
        synchronized (appMutex) {
            try {
                appKieSession.getAgenda().getAgendaGroup("app").setFocus();
                appKieSession.fireAllRules();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // app-long agenda-group
        if(LocalDateTime.now().compareTo(appLongLastTimeFired.plusHours(1)) > 0) {
            synchronized (appLongMutex) {
                appLongKieSession = getKieSession();
                appLongKieSession.setGlobal("maliciousIpAddresses", appKieSession.getGlobal("maliciousIpAddresses"));
                // insertuj u sesiju login alarme za poslednjih 24h npr
                // za svaki slucaj oduzmemo jos neko vreme -------------------------------|
                Date date = Date.from(LocalDateTime.now().minusDays(1).minusHours(1)//  <-|
                        .atZone(ZoneId.systemDefault()).toInstant());
                List<Log> logs = logsRepository.findByCategoryAndTimestampGreaterThan(
                        LogCategory.LOGIN, date);
                if (!logs.isEmpty()) {
                    logs.forEach(appLongKieSession::insert);
                    appLongKieSession.getAgenda().getAgendaGroup("app-long").setFocus();
                    appLongKieSession.fireAllRules();
                    appLongLastTimeFired = LocalDateTime.now();
                }
            }
        }

        // antivirus agenda-group
        if(LocalDateTime.now().compareTo(antivirusLastTimeFired.plusMinutes(10)) > 0) {
            synchronized (antivirusMutex) {
                antivirusKieSession = getKieSession();
                // insertuj u sesiju antivirus alarme za poslednjih sat vremena npr
                Date antivirusDate = Date.from(LocalDateTime.now().minusHours(1).minusMinutes(10)
                        .atZone(ZoneId.systemDefault()).toInstant());
                List<Log> antivirusLogs = logsRepository.findByCategoryAndTimestampGreaterThan(
                        LogCategory.ANTIVIRUS, antivirusDate);
                if (!antivirusLogs.isEmpty()) {
                    antivirusLogs.forEach(antivirusKieSession::insert);
                    antivirusKieSession.getAgenda().getAgendaGroup("antivirus").setFocus();
                    antivirusKieSession.fireAllRules();
                    antivirusLastTimeFired = LocalDateTime.now();
                }
            }
        }

        // antivirus-long agenda-group
        if(LocalDateTime.now().compareTo(antivirusLongLastTimeFired.plusDays(1)) > 0) {
            synchronized (antivirusLongMutex) {
                antivirusLongKieSession = getKieSession();
                // insertuj u sesiju antivirus alarme za poslednjih par dana(vidi pravila)
                Date antivirusLongDate = Date.from(LocalDateTime.now().minusDays(7).minusHours(1)
                        .atZone(ZoneId.systemDefault()).toInstant());
                List<Log> antivirusLongLogs = logsRepository.findByCategoryAndTimestampGreaterThan(
                        LogCategory.ANTIVIRUS, antivirusLongDate);
                if (!antivirusLongLogs.isEmpty()) {
                    antivirusLongLogs.forEach(antivirusLongKieSession::insert);
                    antivirusLongKieSession.getAgenda().getAgendaGroup("antivirus-long").setFocus();
                    antivirusLongKieSession.fireAllRules();
                    antivirusLongLastTimeFired = LocalDateTime.now();
                }
            }
        }
    }

    @Override
    public PageableDto<Log> getSessionLogs(String column, String value, int pageNumber, int pageSize)
            throws ValidationException{
        // 0) Validate
        if(value.isEmpty()) {
            throw new ValidationException("Filter param can not be empty");
        }
        if(pageSize < 1) {
            throw new ValidationException("Page size can not be lower than 1");
        }
        if(pageNumber < 0) {
            throw new ValidationException("Page number can not be lower than 0");
        }

        // 1) Get logs
        QueryResults queryResults;
        switch (column) {
            case Constants.LogFields.type:
                synchronized (appMutex) {
                    queryResults = appKieSession.getQueryResults("Get logs by type", value);
                    break;
                }
            case Constants.LogFields.category:
                synchronized (appMutex) {
                    queryResults = appKieSession.getQueryResults("Get logs by category", value);
                    break;
                }
            case Constants.LogFields.source:
                synchronized (appMutex) {
                    queryResults = appKieSession.getQueryResults("Get logs by source", value);
                    break;
                }
            case Constants.LogFields.host_address:
                synchronized (appMutex) {
                    queryResults = appKieSession.getQueryResults("Get logs by host_address", value);
                    break;
                }
            case Constants.LogFields.message:
                synchronized (appMutex) {
                    queryResults = appKieSession.getQueryResults("Get logs by message", value);
                    break;
                }
            default:
                throw new ValidationException("Column is not valid");
        }

        int resultsCount = queryResults.size();
        int startIndex = pageNumber * pageSize;
        if(startIndex >= resultsCount) {
            return new PageableDto<>();
        }
        int endIndex = startIndex + pageSize;
        if(endIndex >= resultsCount) {
            endIndex = resultsCount;
        }
        boolean isFirstPage = pageNumber == 0;
        int numberOfPages = resultsCount / pageSize;
        if(resultsCount % pageSize != 0) {
            numberOfPages++;
        }
        boolean isLastPage = pageNumber == numberOfPages - 1;

        // 2) Sort by timestamp
        List<Log> result = new ArrayList<>();
        queryResults.forEach(x -> result.add((Log)x.get("$l")));
        result.sort(Comparator.comparing(Log::getTimestamp));
        Collections.reverse(result);

        // 3) Take range
        return new PageableDto<>(result.subList(startIndex, endIndex), isFirstPage, isLastPage, numberOfPages);
    }

    @Override
    public Page<Alarm> getAlarms(Pageable pageable) {
        return alarmRepository.findAllWithPagination(pageable);
    }

    private void validateLoginTemplateRequestDto(LoginTemplateDto loginTemplateDto) throws ValidationException {
        if(loginTemplateDto.getLoginAttemptCount() < 1) {
            throw new ValidationException("Login attempt count can not be lower than 1");
        }
        if(!loginTemplateDto.getHostRelation().equals("==") && !loginTemplateDto.getHostRelation().equals("!=")) {
            throw new ValidationException("Host relation must be '==' or '!='");
        }
        if(!loginTemplateDto.getSourceRelation().equals("==") && !loginTemplateDto.getSourceRelation().equals("!=")) {
            throw new ValidationException("Source relation must be '==' or '!='");
        }
        if(loginTemplateDto.getTimeCount() < 1) {
            throw new ValidationException("Time count can not be lower than 1");
        }
        List<String> possibleTimeUnits = new ArrayList<>(Arrays.asList("s", "m", "h", "d", "M", "y"));
        if(!possibleTimeUnits.contains(loginTemplateDto.getTimeUnit())) {
            throw new ValidationException("Time unit is not valid");
        }
    }

    @Scheduled(fixedRate = 3000, initialDelay = 10000)
    public void retrieveAlarms() {
        synchronized (appMutex) {
            retrieveAlarmsFromSession(appKieSession);
        }

        synchronized (appLongMutex) {
            if (appLongKieSession != null) {
                retrieveAlarmsFromSession(appLongKieSession);
                appLongKieSession.dispose();
                appLongKieSession = null;
            }
        }

        synchronized (antivirusMutex) {
            if (antivirusKieSession != null) {
                retrieveAlarmsFromSession(antivirusKieSession);
                antivirusKieSession.dispose();
                antivirusKieSession = null;
            }
        }

        synchronized (antivirusLongMutex) {
            if (antivirusLongKieSession != null) {
                retrieveAlarmsFromSession(antivirusLongKieSession);
                antivirusLongKieSession.dispose();
                antivirusLongKieSession = null;
            }
        }
    }

    private void retrieveAlarmsFromSession(KieSession kieSession) {
        QueryResults results = kieSession.getQueryResults("Get all alarms");
        if(results.size() == 0) {
            return;
        }
        // save new alarms
        for (QueryResultsRow queryResult : results) {
            Alarm a = (Alarm) queryResult.get("$a");
            alarmRepository.save(a);
        }
        // prevent alarms from retrieving again
        kieSession.getAgenda().getAgendaGroup("alarm").setFocus();
        kieSession.fireAllRules();
        // revert focus
        kieSession.getAgenda().getAgendaGroup("MAIN").setFocus();
        // send on view
        for (QueryResultsRow queryResult : results) {
            Alarm a = (Alarm) queryResult.get("$a");
            producer.sendMessage(a);
        }
    }
}
