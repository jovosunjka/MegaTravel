package com.bsep.SIEMCenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class SiemCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiemCenterApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {
		String certificatesTrustStorePath = "C:\\Program Files\\Java\\jre1.8.0_201\\lib\\security\\cacerts";
		System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
	}

}
