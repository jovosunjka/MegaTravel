package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.controller.dto.LoginTemplateDto;
import com.bsep_sbz.SIEMCenter.controller.dto.PageableDto;
import com.bsep_sbz.SIEMCenter.helper.Constants;
import com.bsep_sbz.SIEMCenter.helper.HelperMethods;
import com.bsep_sbz.SIEMCenter.helper.ValidationException;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Alarm;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.repository.AlarmRepository;
import com.bsep_sbz.SIEMCenter.repository.LogsRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import com.bsep_sbz.SIEMCenter.util.DebugAgendaEventListener;
import com.bsep_sbz.SIEMCenter.websockets.Producer;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private KieSession kieSession;
    private static int loginTemplateCounter = 0;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSessions() {
        kieSession = kieContainer.newKieSession("logs-session");
        kieSession.addEventListener(new DebugAgendaEventListener(kieSession, alarmRepository, producer));
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
    public void generateRule(LoginTemplateDto loginTemplateDto)
            throws ValidationException, IOException, MavenInvocationException {
        // 0) Validate request
        validateLoginTemplateRequestDto(loginTemplateDto);
        // WARNING !!! Kada se pokrene spring-boot:run working directory je tamo gde se nalazi pom.xml pokrenutog projekta,
        // a ako se pokrene u debug rezimu onda je working directory MegaTravel root folder !!!
        String templatePath = "..\\SiemCenterRules\\src\\main\\resources\\sbz\\" +
                "rules\\templates\\login_attempt.drt";
        String drlPath = "..\\SiemCenterRules\\src\\main\\resources\\sbz\\" +
                "rules\\template_rules\\LoginAttemptRule" + loginTemplateCounter++ + ".drl";

        // 1) Read template
        InputStream template = new FileInputStream(templatePath);

        // 2) Compile template to drl rule
        List<LoginTemplateDto> data = new ArrayList<>();
        data.add(loginTemplateDto);
        String drl = (new ObjectDataCompiler()).compile(data, template);

        // 3) Save drl rule to file
        Files.write( Paths.get(drlPath), drl.getBytes(), StandardOpenOption.CREATE);

        // 4) maven clean, maven install
        HelperMethods.mavenCleanAndInstallRules();
    }

    @Override
    public void insertInSession(List<Log> logRet) {
        logRet.forEach(kieSession::insert);
        kieSession.fireAllRules();
    }

    @Override
    public PageableDto<Log> getSessionLogs(String column, String value, int pageNumber, int pageSize)
            throws ValidationException{
        // 0) Validate
        if(!isColumnValid(column)) {
            throw new ValidationException("Column is not valid");
        }
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
        QueryResults queryResults = kieSession.getQueryResults("Get logs by message", value);

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

    private boolean isColumnValid(String column) {
        switch (column) {
            case Constants.LogFields.id:
            case Constants.LogFields.type:
            case Constants.LogFields.category:
            case Constants.LogFields.source:
            case Constants.LogFields.timestamp:
            case Constants.LogFields.host_address:
            case Constants.LogFields.message:
                return true;
            default:
                return false;
        }
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
}
