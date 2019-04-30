package com.bsep_sbz.PKI.service.certificate;

import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 *Cita sertifikat iz fajla
 */
@Service
public class CertificateReaderServiceImpl implements CertificateReaderService {

	public static final String BASE64_ENC_CERT_FILE = "./model/jovan2.cer";
	public static final String BIN_ENC_CERT_FILE = "./model/jovan1.cer";
	
	public void testIt() {
		System.out.println("Cita sertifikat iz Base64 formata");
		readFromBase64EncFile();
		System.out.println("\n\nCita sertifikat iz binarnog formata");
		readFromBinEncFile();
	}
	
	@Override
	public void readFromBase64EncFile() {
		try {
			FileInputStream fis = new FileInputStream(BASE64_ENC_CERT_FILE);
			 BufferedInputStream bis = new BufferedInputStream(fis);

			 CertificateFactory cf = CertificateFactory.getInstance("X.509");

			 //Cita sertifikat po sertifikat
			 //Svaki certifikat je izmedju 
			 //-----BEGIN CERTIFICATE-----, 
			 //i
			 //-----END CERTIFICATE-----. 
			 while (bis.available() > 0) {
			    Certificate cert = cf.generateCertificate(bis);
			    System.out.println(cert.toString());
			 }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void readFromBinEncFile() {
		try {
			FileInputStream fis = new FileInputStream(BIN_ENC_CERT_FILE);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			//Ovde se vade svi sertifkati
			Collection c = cf.generateCertificates(fis);
			Iterator i = c.iterator();
			while (i.hasNext()) {
			    Certificate cert = (Certificate)i.next();
			    System.out.println(cert);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		CertificateReaderServiceImpl test = new CertificateReaderServiceImpl();
		test.testIt();
	}
}
