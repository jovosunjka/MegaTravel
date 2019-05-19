package com.bsep_sbz.SIEMCenter.unit_test.template;

import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.unit_test.template.model.AttackData;
import org.codehaus.plexus.util.StringUtils;
import org.drools.template.*;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import static org.junit.Assert.assertEquals;

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
}
