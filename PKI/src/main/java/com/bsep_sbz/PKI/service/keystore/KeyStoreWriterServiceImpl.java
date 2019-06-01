package com.bsep_sbz.PKI.service.keystore;

import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.List;

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
		FileInputStream fis = null;

		try {
			if(fileOrFileName != null) {
				if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
				else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
				else throw new Exception("Argument fileOrFileName must be String or File!");
				keyStore.load(fis, password);
				fis.close();
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
		finally {
			try {
				if(fis != null) fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void saveKeyStore(Object fileOrFileName, char[] password) {
		FileOutputStream fos = null;
		try {
			if(fileOrFileName instanceof String) fos = new FileOutputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fos = new FileOutputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			keyStore.store(fos, password);
			fos.close();
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
		finally {
			try {
				if(fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	@Override
	public void writeCertificates(Object fileOrFileName, char[] password, List<String> aliases, List<Certificate> certificates) throws Exception {
		if(aliases.size() != certificates.size()) {
			throw new Exception("Moramo imati isti broj aliasa i sertifikata");
		}

		//kreiramo instancu KeyStore
		KeyStore ks = KeyStore.getInstance("JKS", "SUN");
        //Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
        ks.load(null, password);

		for(int i = 0; i < aliases.size(); i++) {
			ks.setCertificateEntry(aliases.get(i).toLowerCase(), certificates.get(i));
		}

		FileOutputStream fos;
		if(fileOrFileName instanceof String) fos = new FileOutputStream((String) fileOrFileName);
		else if(fileOrFileName instanceof File) fos = new FileOutputStream((File) fileOrFileName);
		else throw new Exception("Argument fileOrFileName must be String or File!");

		try {
			if(fileOrFileName instanceof String) fos = new FileOutputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fos = new FileOutputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			ks.store(fos, password);
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
		finally {
			try {
				if(fos != null) fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
