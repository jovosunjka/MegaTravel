package com.bsep_sbz.SIEMCenter.service.keystore;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
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
			Path filePath;
			if(fileOrFileName instanceof String) {
				String fileName = (String) fileOrFileName;
				fos = new FileOutputStream(fileName);
				filePath = Paths.get(fileName);
			}
			else if(fileOrFileName instanceof File) {
				File file = (File) fileOrFileName;
				fos = new FileOutputStream(file);
				filePath = file.toPath();
			}
			else throw new Exception("Argument fileOrFileName must be String or File!");
			keyStore.store(fos, password);
			addFilePermissions(filePath);
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

	// https://docs.oracle.com/javase/8/docs/api/java/nio/file/attribute/AclFileAttributeView.html
	private void addFilePermissions(Path filePath) throws IOException {
		// lookup "siem_center"
		UserPrincipal siemCenter = filePath.getFileSystem().getUserPrincipalLookupService()
								.lookupPrincipalByName("siem_center");

		// get view
		AclFileAttributeView viewFile = Files.getFileAttributeView(filePath, AclFileAttributeView.class);

		// create ACE
		AclEntry entry = AclEntry.newBuilder()
				//.setType(AclEntryType.ALLOW)
				.setType(AclEntryType.DENY)
				.setPrincipal(siemCenter)
				.setPermissions(AclEntryPermission.READ_DATA, AclEntryPermission.READ_ATTRIBUTES,
								AclEntryPermission.WRITE_DATA, AclEntryPermission.EXECUTE)
				.build();

		// read ACL, insert ACE, re-write ACL
		List<AclEntry> aclForFIle = viewFile.getAcl();
		aclForFIle.add(0, entry);   // insert before any DENY entries
		viewFile.setAcl(aclForFIle);

		Path directoryPath = filePath.toFile().getParentFile().toPath();
		AclFileAttributeView viewDirectory = Files.getFileAttributeView(directoryPath, AclFileAttributeView.class);

		// create ACE
		AclEntry entryForDirectory = AclEntry.newBuilder()
				.setType(AclEntryType.ALLOW)
				//.setType(AclEntryType.DENY)
				.setPrincipal(siemCenter)
				.setPermissions(AclEntryPermission.LIST_DIRECTORY)
				.build();

		// read ACL, insert ACE, re-write ACL
		List<AclEntry> aclForDirectory = viewDirectory.getAcl();
		aclForDirectory.add(0, entryForDirectory);   // insert before any DENY entries
		viewFile.setAcl(aclForDirectory);
	}


	private File createFileWithPermissions(String filePathStr) throws IOException {
		List<AclEntry> entries = new ArrayList<AclEntry>();

		// lookup "joe"
		UserPrincipal siemCenter = Paths.get(filePathStr).getFileSystem().getUserPrincipalLookupService()
										.lookupPrincipalByName("siem_center");

		// create ACE to give "joe" read access
		AclEntry entry = AclEntry.newBuilder()
				.setType(AclEntryType.ALLOW)
				.setPrincipal(siemCenter)
				.setPermissions(AclEntryPermission.READ_DATA, AclEntryPermission.READ_ATTRIBUTES)
				.build();
		entries.add(entry);

		Path filePath = Files.createFile(Paths.get(filePathStr), new FileAttribute() {
			@Override
			public String name() {
				return "acl:acl";
			}

			@Override
			public Object value() {
				return entries;
			}
		});

		return filePath.toFile();
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
