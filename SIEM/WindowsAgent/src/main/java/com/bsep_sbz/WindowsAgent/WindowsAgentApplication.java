package com.bsep_sbz.WindowsAgent;

import com.bsep_sbz.WindowsAgent.helper.Constants;
import com.sun.jna.platform.win32.Advapi32Util;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.sun.jna.platform.win32.Advapi32Util.EventLogIterator;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;


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
		EventLogIterator iter = new EventLogIterator(Constants.Application);
		while(iter.hasNext()) {
			Advapi32Util.EventLogRecord record = iter.next();
			System.out.println(record.getRecordNumber()
					+ ": Event ID: " + record.getEventId()
					+ ", Event Type: " + record.getType()
					+ ", Event Source: " + record.getSource());
		}
	}
}
