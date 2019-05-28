package com.bsep_sbz.WindowsAgent;

import com.bsep_sbz.WindowsAgent.helper.Constants;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinNT;
//import com.sun.jna.platform.win32.Advapi32Util.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SpringBootApplication
public class WindowsAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(WindowsAgentApplication.class, args);
	}

	//@EventListener(ApplicationReadyEvent.class)
	public void startupCallback() {
		new Thread(WindowsAgentApplication::watchSysLogs).start();
	}

	private static void watchSysLogs() {
		// Note: if you want to read security logs you must run your ide in administrator mode
		EventLogIterator iter = new EventLogIterator(Constants.Security);
		ArrayList<Advapi32Util.EventLogRecord> recordList = new ArrayList<>();
		while(iter.hasNext()) {
			recordList.add(iter.next());
		}
		/*
			System.out.println(record.getRecordNumber()
			+ ": Event ID: " + record.getEventId()
			+ ", Event Type: " + record.getType()
			+ ", Event Source: " + record.getSource());
		 */
		Collections.sort(recordList, new Comparator<Advapi32Util.EventLogRecord>(){
			public int compare(Advapi32Util.EventLogRecord o1, Advapi32Util.EventLogRecord o2){
				if(o1.getRecord().TimeWritten.intValue() == o2.getRecord().TimeWritten.intValue())
					return 0;
				return o1.getRecord().TimeWritten.intValue() < o2.getRecord().TimeWritten.intValue() ? 1 : -1;
			}
		});
		ArrayList<Advapi32Util.EventLogRecord> filteredList = new ArrayList<>();
		ArrayList<String[]> strings = new ArrayList<>();
		byte[] data = recordList.get(0).getData();
		WinNT.EVENTLOGRECORD record = recordList.get(0).getRecord();
		String str = record.getPointer().getString(record.StringOffset.longValue());
		Object obj = record.readField("EventCategory");
		int counter = 0;
		for(Advapi32Util.EventLogRecord rec : recordList)
		{
			if(rec.getData() != null)
			{
				filteredList.add(rec);
			}
			strings.add(rec.getStrings());
			System.out.println(rec.getRecordNumber()
					+ ": Event ID: " + rec.getEventId()
					+ ", Event Type: " + rec.getType()
					+ ", Event Source: " + rec.getSource());
		}
		System.out.println();
	}
}
