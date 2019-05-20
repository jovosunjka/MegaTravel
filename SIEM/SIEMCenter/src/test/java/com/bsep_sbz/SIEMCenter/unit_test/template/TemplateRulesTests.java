package com.bsep_sbz.SIEMCenter.unit_test.template;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.sbz.KnowledgeSessionHelper;
import com.bsep_sbz.SIEMCenter.unit_test.template.model.AttackData;
import com.bsep_sbz.SIEMCenter.unit_test.template.model.LoginData;
import org.codehaus.plexus.util.StringUtils;
import org.drools.template.*;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TemplateRulesTests {

    @Test
    public void testAttackTemplateCreation() throws IOException {
        InputStream template = new FileInputStream(
                "..\\SiemCenterRules\\src\\main\\resources\\sbz\\rules\\templates\\attack.drt");

        int logCount = 50;
        String time = "60s";
        List<AttackData> data = new ArrayList<>();
        data.add(new AttackData(LogCategory.PAYMENT_SYSTEM.name(), logCount, time));
        data.add(new AttackData(LogCategory.LOGIN.name(), logCount, time)); // brute force
        data.add(new AttackData(LogCategory.APP.name(), logCount, time)); // DOS

        ObjectDataCompiler compiler = new ObjectDataCompiler();

        // Act
        String drl = compiler.compile(data, template);
        System.out.println(drl);

        // Assert
        int numOfPaymentSystemStrs = StringUtils.countMatches(drl, LogCategory.PAYMENT_SYSTEM.name());
        assertEquals(2, numOfPaymentSystemStrs);
        int numOfAppStrs = StringUtils.countMatches(drl, LogCategory.APP.name());
        assertEquals(2, numOfAppStrs);
        int numOfLoginStrs = StringUtils.countMatches(drl, LogCategory.LOGIN.name());
        assertEquals(2, numOfLoginStrs);
        int numOfLogCountStrs = StringUtils.countMatches(drl, Integer.toString(logCount));
        assertEquals(3, numOfLogCountStrs);
        int numOfTimeStrs = StringUtils.countMatches(drl, time);
        assertEquals(3, numOfTimeStrs);
    }

    @Test
    public void testLoginTemplateCreation() throws IOException{
        // Arrange
        InputStream template = new FileInputStream(
                "..\\SiemCenterRules\\src\\main\\resources\\sbz\\" +
                        "rules\\templates\\different_sources_but_same_host_login_attempt.drt");

        List<LoginData> data = new ArrayList<>();
        data.add(new LoginData(2,10,"s"));
        data.add(new LoginData(30,5,"h"));

        ObjectDataCompiler compiler = new ObjectDataCompiler();

        // Act
        String drl = compiler.compile(data, template);
        System.out.println(drl);

        // Assert
        int numOfRules = StringUtils.countMatches(drl, "when");
        assertEquals(2, numOfRules);
        assertTrue(drl.contains("intValue >= 2"));
        assertTrue(drl.contains("intValue >= 30"));
        assertTrue(drl.contains("over window:time(5h)"));
        assertTrue(drl.contains("over window:time(10s)"));
    }
}
