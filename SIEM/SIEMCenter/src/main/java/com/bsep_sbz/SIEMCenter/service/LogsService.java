package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.controller.dto.LoginTemplateDto;
import com.bsep_sbz.SIEMCenter.helper.Constants;
import com.bsep_sbz.SIEMCenter.helper.HelperMethods;
import com.bsep_sbz.SIEMCenter.helper.ValidationException;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.repository.LogsRepository;
import com.bsep_sbz.SIEMCenter.service.interfaces.ILogsService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.drools.template.ObjectDataCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LogsService implements ILogsService
{
    @Autowired
    private LogsRepository logsRepository;

    @Autowired
    private KieContainer kieContainer;

    private KieSession kieSession;

    public LogsService() {
        kieSession = kieContainer.getKieBase().newKieSession();
    }

    private static int loginTemplateCounter = 0;

    public void save(List<Log> logs) {
        logs.forEach(logsRepository::save);
    }

    @Override
    public Page<Log> getRange(Pageable pageable) {
        return logsRepository.findAll(pageable);
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
            case Constants.id:
                return logsRepository.findByIdRegexAndPagination(regExp, pageable);
            case Constants.type:
                return logsRepository.findByTypeRegexAndPagination(regExp, pageable);
            case Constants.category:
                return logsRepository.findByCategoryRegexAndPagination(regExp, pageable);
            case Constants.source:
                return logsRepository.findBySourceRegexAndPagination(regExp, pageable);
            case Constants.timestamp:
                return logsRepository.findByTimestampRegexAndPagination(regExp, pageable);
            case Constants.host_address:
                return logsRepository.findByHostAddressRegexAndPagination(regExp, pageable);
            case Constants.message:
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
