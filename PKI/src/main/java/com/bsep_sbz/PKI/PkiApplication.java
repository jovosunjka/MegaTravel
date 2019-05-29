package com.bsep_sbz.PKI;

import com.bsep_sbz.PKI.config.ftp.MyGateway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;


@SpringBootApplication
public class PkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);
	}


	/*public static void main(String[] args) {
		ConfigurableApplicationContext context =
				new SpringApplicationBuilder(PkiApplication.class)
						//.web(WebApplicationType.NONE)
						.run(args);

		//System.setProperty("jdk.tls.useExtendedMasterSecret", "false");

		System.out.println("Saljem...");
		MyGateway gateway = context.getBean(MyGateway.class);
		gateway.sendToFtp(new File("C:\\bsep_sbz_workspace\\MegaTravel\\PKI\\src\\main\\resources\\foo\\bar.txt"));

		System.out.println("Poslao");
	}*/

}
