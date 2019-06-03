package com.bsep_sbz.PKI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.net.ssl.SSLServerSocketFactory;
import java.util.Map;
import java.util.TreeMap;


@SpringBootApplication
public class PkiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PkiApplication.class, args);
	}



	// https://security.stackexchange.com/questions/166063/tls-rsa-vs-tls-ecdhe-rsa-vs-static-dh
	// https://stackoverflow.com/questions/48934210/java-ssl-error-cannot-support-tls-ecdhe-rsa-with-aes-256-gcm-sha384
	@EventListener(ApplicationReadyEvent.class)
	public void aaa() {
		// ovaj kod ce izlistati sve podrzane chipers
		SSLServerSocketFactory ssf = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();

		TreeMap<String, Boolean> ciphers = new TreeMap<>();
		for (String cipher : ssf.getSupportedCipherSuites())
			ciphers.put(cipher, Boolean.FALSE);
		for (String cipher : ssf.getDefaultCipherSuites())
			ciphers.put(cipher, Boolean.TRUE);

		System.out.println("Default Cipher");
		for (Map.Entry<String, Boolean> cipher : ciphers.entrySet())
			System.out.printf("   %-5s%s%n", (cipher.getValue() ? '*' : ' '), cipher.getKey());
	}

}
