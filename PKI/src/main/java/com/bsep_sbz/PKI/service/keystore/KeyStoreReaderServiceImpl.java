package com.bsep_sbz.PKI.service.keystore;

import com.bsep_sbz.PKI.model.IssuerData;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeyStoreReaderServiceImpl implements KeyStoreReaderService {
	//KeyStore je Java klasa za citanje specijalizovanih datoteka koje se koriste za cuvanje kljuceva
	//Tri tipa entiteta koji se obicno nalaze u ovakvim datotekama su:
	// - Sertifikati koji ukljucuju javni kljuc
	// - Privatni kljucevi
	// - Tajni kljucevi, koji se koriste u simetricnima siframa
	private KeyStore keyStore;
	
	public KeyStoreReaderServiceImpl() {
		try {
			keyStore = KeyStore.getInstance("JKS", "SUN");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
	 * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
	 * 
	 * @param fileOrFileName - datoteka odakle se citaju podaci
	 * @param alias - alias putem kog se identifikuje sertifikat izdavaoca
	 * @param password - lozinka koja je neophodna da se otvori key store
	 * @param keyPass - lozinka koja je neophodna da se izvuce privatni kljuc
	 * @return - podatke o izdavaocu i odgovarajuci privatni kljuc
	 */
	@Override
	public IssuerData readIssuerFromStore(Object fileOrFileName, String alias, char[] password, char[] keyPass) {
        FileInputStream fis = null;
        PrivateKey privKey = null;
        X500Name issuerName = null;
	    try {
			//Datoteka se ucitava
			if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			BufferedInputStream in = new BufferedInputStream(fis);
			keyStore.load(in, password);
			//Iscitava se sertifikat koji ima dati alias
			Certificate cert = keyStore.getCertificate(alias);
			//Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
			privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

			//issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
			issuerName = getIssuerName((X509Certificate) cert);

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
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

        return new IssuerData(privKey, issuerName);
	}


	@Override
	public IssuerData getIssuerData(Object fileOrFileName, String alias, char[] password, char[] keyPass, X509Certificate certificate) {
		PrivateKey privateKey = readPrivateKey(fileOrFileName, keyPass, alias, password);
		X500Name issuerName = getIssuerName(certificate);

		if(privateKey == null || issuerName == null) return null;

		return new IssuerData(privateKey, issuerName);
	}

	private X500Name getIssuerName(X509Certificate certificate) {
		try {
			return new JcaX509CertificateHolder(certificate).getIssuer();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Ucitava sertifikat is KS fajla
	 */
	@Override
    public Certificate readCertificate(Object fileOrFileName, char[] keyStorePass, String alias) {
        FileInputStream fis = null;
	    try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			BufferedInputStream in = new BufferedInputStream(fis);
			ks.load(in, keyStorePass);

			if(ks.isKeyEntry(alias)) {
				Certificate cert = ks.getCertificate(alias);
				return cert;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
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
		return null;
	}

	@Override
	public List<Certificate> readCertificates(Object fileOrFileName, char[] keyStorePass, List<String> aliases, boolean trustStore) {
        FileInputStream fis = null;
	    try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			BufferedInputStream in = new BufferedInputStream(fis);
			ks.load(in, keyStorePass);

			if(aliases == null) {
				aliases = Collections.list(ks.aliases());
			}

			Certificate cert;
			List<Certificate> certificates = new ArrayList<Certificate>();

			boolean tmp;
			for(String alias : aliases) {
				if(trustStore) {
					tmp = ks.isCertificateEntry(alias);
				}
				else {
					tmp = ks.isKeyEntry(alias);
				}

				if(tmp) {
					cert = ks.getCertificate(alias);
					certificates.add(cert);
				}
				else {
					throw new Exception("Alias '"+alias+"' ne postoji!");
				}
			}

			return certificates;

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
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
		return null;
	}

	@Override
	public int getNumOfAliases(Object fileOrFileName, char[] keyStorePass) {
		FileInputStream fis = null;
		try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			BufferedInputStream in = new BufferedInputStream(fis);
			ks.load(in, keyStorePass);

			List<String> aliases = Collections.list(ks.aliases());
			return aliases.size();

		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
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
		return 0;
	}

	/**
	 * Ucitava privatni kljuc is KS fajla
	 */
	@Override
	public PrivateKey readPrivateKey(Object fileOrFileName, char[] keyStorePass, String alias, char[] pass) {
        FileInputStream fis = null;

	    try {
			//kreiramo instancu KeyStore
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			//ucitavamo podatke
			if(fileOrFileName instanceof String) fis = new FileInputStream((String) fileOrFileName);
			else if(fileOrFileName instanceof File) fis = new FileInputStream((File) fileOrFileName);
			else throw new Exception("Argument fileOrFileName must be String or File!");
			BufferedInputStream in = new BufferedInputStream(fis);
			ks.load(in, keyStorePass);
			
			if(ks.isKeyEntry(alias)) {
				PrivateKey pk = (PrivateKey) ks.getKey(alias, pass);
				return pk;
			}
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
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

		return null;
	}
}
