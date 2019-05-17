package com.bsep_sbz.WindowsAgent.service;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bsep_sbz.WindowsAgent.controller.dto.MessageDto;
import com.bsep_sbz.WindowsAgent.model.Address;
import com.bsep_sbz.WindowsAgent.model.Addresses;
import com.bsep_sbz.WindowsAgent.service.interfaces.ILogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
public class LogsService implements ILogsService {

	@Autowired
	private RestTemplate _restTemplate;

	public List<String> logFilter(List<String> logs) {

		List<String> logRet = new ArrayList<>();
    	HashMap<String, String> include = new HashMap<>();
    	HashMap<String, String> exclude = new HashMap<>();
    	boolean flag = false; 
        if (logs!=null && logs.size() != 0) {
        	for (String log: logs) {
        		for (String key : include.keySet()) {
        			 Pattern patternInclude = Pattern.compile(key);
        			 Matcher matcherInc = patternInclude.matcher(log);
        		        if (matcherInc.matches()) {
        		        	
        		        	for (String keyEx : exclude.keySet()) {
        		        		Pattern patternExclude = Pattern.compile(keyEx);
        	        			Matcher matcherExc = patternExclude.matcher(log);
        	        			if (matcherExc.matches()) flag = true;
        		        	}
        		        	if (!flag) { 
        		        		logRet.add(log+"|"+ include.get(key));
        		        		
        		        	}
        		        	flag = false;
        		        	
        		        	}
        		        	
        		        }
        		        
        		}
        	}    
        
        return logRet;
    	
		
	}

	public void sendLogs(List<String> logs) throws IOException
	{
		System.out.println("entered");

		if(logs.isEmpty()) {
			System.out.println("Nema nijedan log za slanje!");
			return;
		}

		try {
			HttpEntity<List<String>> httpEntity = new HttpEntity<List<String>>(logs);
			ResponseEntity responseEntity = _restTemplate.postForEntity("https://localhost:8081/api/logs/process",
					httpEntity, Void.class);
			if(responseEntity.getStatusCode() == HttpStatus.OK) {
				System.out.println("Uspesno su poslati logovi! (" + LocalTime.now().toString() + ")");
			}
			else {
				System.out.println("Neuspesno su poslati logovi! (" + LocalTime.now().toString() + ")");
			}

		}
		catch (Exception exception) {
			System.out.println("GRESKA PRILIKOM SLANJA LOGOVA SIEM CENTRU!!!");
			exception.printStackTrace();
		}
	}



}
