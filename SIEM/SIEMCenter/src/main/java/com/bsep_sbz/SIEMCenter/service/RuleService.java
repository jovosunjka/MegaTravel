package com.bsep_sbz.SIEMCenter.service;

import com.bsep_sbz.SIEMCenter.helper.HelperMethods;
import com.bsep_sbz.SIEMCenter.model.sbz.log.Log;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogCategory;
import com.bsep_sbz.SIEMCenter.model.sbz.enums.log.LogLevel;
import com.bsep_sbz.SIEMCenter.service.interfaces.IRuleService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RuleService implements IRuleService
{
    public List<Log> makeLogs(List<String> logs) {
        //# log id|event id|timestamp|log lvl type|message
        List<Log> logsRet = new ArrayList<>();
        for (String log: logs) {
            String[] tokens = log.split("\\|");
            try {
                Long id = Long.parseLong(getValue(tokens,"id"));
                long timestamp = Long.parseLong(getValue(tokens,"timestamp"));
                Date timestampDate = new Date(timestamp);
                Date now = new Date();
                if(timestampDate.after(now)) {
                    throw new Exception("Log timestamp is in past");
                }
                String hostAddress = getValue(tokens, "hostaddress");
                LogLevel logLevel = LogLevel.valueOf(getValue(tokens, "loglevel"));
                LogCategory logCategory = LogCategory.valueOf(getValue(tokens, "logcategory"));
                String message = getValue(tokens, "message");
                String source = getSource(message);
                Log l = new Log(id, logLevel, logCategory, timestampDate, source, hostAddress, message);
                l.setUsername(HelperMethods.getUsername(message));
                logsRet.add(l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return logsRet;
    }

    private String getValue(String[] tokens, String key) {
        Optional<String> optional = Arrays.stream(tokens).filter(x -> x.startsWith(key)).findFirst();
        return optional.map(s -> s.split(":", 2)[1].trim()).orElse("");
    }

    private String getSource(String message) {
        if(message.contains("ip address"))
        {
            String[] splittedMessage = message.split("\\s+");
            int i = 0;
            for(; i<splittedMessage.length; i++) {
                if(splittedMessage[i].equals("address")) {
                    break;
                }
            }
            String ipAddress = splittedMessage[++i];
            return ipAddress.replaceAll("'", "");
        }

        return HelperMethods.getUsername(message);
    }

}