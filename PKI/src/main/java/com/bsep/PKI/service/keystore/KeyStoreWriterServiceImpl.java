package com.bsep.PKI.service.keystore;

import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class KeyStoreWriterServiceImpl implements KeyStoreWriterService {
	//KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
	//Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
	// - Sertifikati koji ukljucuju javni kljuc
	// - Privatni kljucevi
	// - Tajni kljucevi, koji se koriste u simetricnima siframa
	private KeyStore keyStore;
	
	public KeyStoreWriterServiceImpl() {
		try {
			keyStore = KeyStore.getInstance("JKS", "SUN");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadKeyStore(Object fileOrFileName, char[] password) {
		try {
			if(fileOrFileName != null) {
				FileInputStream fis;
				if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
				else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
				else throw new Exception("Argument fileOrFileName must be String or File!");
				keyStore.load(fis, password);
			} else {
				//Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
				keyStore.load(null, password);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveKeyStore(Object fileOrFileName, char[] password) {
		try {
			FileOutputStream fos;
			if(fileOrFileName instanceof String) fos = new FileOutputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fos = new FileOutputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			keyStore.store(fos, password);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
		try {
			keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeCertificate(String alias, Certificate certificate) {
		try {
			keyStore.setCertificateEntry(alias, certificate);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}

}
