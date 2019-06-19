package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.dto.TrustStoreConfigDTO;
import com.bsep_sbz.PKI.ftp.MyGateway;
import com.bsep_sbz.PKI.model.*;
import com.bsep_sbz.PKI.model.Certificate;
import com.bsep_sbz.PKI.repository.CertificateRepository;
import com.bsep_sbz.PKI.service.certificate.CertificateGeneratorService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreReaderService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreWriterService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private KeyStoreWriterService keyStoreWriterService;

    @Autowired
    private CertificateGeneratorService certificateGeneratorService;

    @Autowired
    private  ApplicationAddressService applicationAddressService;


    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private MyGateway gateway;

    @Autowired
    private FtpApplicationService ftpApplicationService;

    //@Autowired
    //private FTPSClient ftpsClient;

    @Autowired
    private DefaultFtpSessionFactory defaultFtpSessionFactory;


    private FtpApplication rootCA;

    @Value("${main_root_ca}")
    private String MainRootCa;

    @Value("${main_company_unit}")
    private String MainCompanyUnit;

    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private char[] keyStorePassword;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;


    private final String directoryClassesPath = this.getClass().getResource("../../../../").getPath(); // target/classes folder
    private final String directoryTempPath = System.getProperty("java.io.tmpdir");
    private String directoryStoresPath = directoryClassesPath + "stores"; // target/classes/stores folder

    private final String keyStorePath = directoryStoresPath + "/keystore.jks";
    private final String keyStorePath2 = directoryStoresPath + "/siem_center/keystore.jks";
    private final String trustStorePath = directoryStoresPath + "/truststore.jks";

    //private final String keyStorePassword = "key_store_pass";
    //private final String trustStorePassword = "trust_store_pass";

    private final long ROOT_CERTIFICATE_DURATION = 10L; // 10 years
    private final long INTERMEDIATE_CERTIFICATE_DURATION = 5L; // 5 years
    private final long OTHER_CERTIFICATE_DURATION = 2L; // 2 years


    // Kreira folder stores u /target/classes (ako vec nije kreirana).
    // U folderu stores ako kreira dva keyostore-a (ako vec nisu kreirani): certificates_store.jks i private_keys_store.jks

    //@EventListener(ApplicationReadyEvent.class)
    private void prepareStores() throws Exception {
        //System.out.println(directoryStoresResource.getURL().getPath());
        File directoryStores = new File(directoryStoresPath);
        if(!directoryStores.exists()) { // proverice da li postoji i da li je directory
            boolean created = directoryStores.mkdir();
            if(created) {
                System.out.println("The directory " + directoryStoresPath + " was created successfully!");
            }
            else {
                throw new Exception("For some reason, directory " + directoryStoresPath + " is not created!");
            }
        }

        if(!new File(keyStorePath).exists()) {
            //keyStoreWriterService.loadKeyStore(null, keyStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(null, keyStorePassword);
            //keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(keyStore.getFile(), keyStorePassword);
            System.out.println("The keystore " + keyStorePath + " was created successfully!");
        }

        if(!new File(trustStorePath).exists()) {
            //keyStoreWriterService.loadKeyStore(null, trustStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(null, trustStorePassword);
            //keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword);
            System.out.println("The keystore " + trustStorePath + " was created successfully!");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void prepareCertificatesForDataBase() {
        try {
            if(certificateRepository.findAll().size() > 0) return;

            // aliases == null -> aliases = all alliases from keystore
            List<java.security.cert.Certificate> certificates = keyStoreReaderService.readCertificates(trustStore.getFile(), trustStorePassword, null, true);
            List<java.security.cert.Certificate> rootCertificates = keyStoreReaderService.readCertificates(keyStore.getFile(), keyStorePassword, null, false);

            // Za obicne sertifikate nece biti podesen trustStoreCertificates prilikom pokretanja aplikacije
            // Moguce je da je od pre sacuvana konfiguracija za trustorove obicnih sertifikata, u bazi
            List<Certificate> listCertificates = certificates.parallelStream()
                    .map(cert -> getCertificateForDateBase(cert))
                    .collect(Collectors.toList());
            certificateRepository.saveAll(listCertificates);

            // Za root sertifikate nece biti podesen trustStoreCertificates
            List<Certificate> listRootCertificates = rootCertificates.parallelStream()
                    //.filter(cer -> ((X509Certificate) cer).getNotAfter().before(new Date()))
                    .map(cert -> {
                        Certificate rootC = getCertificateForDateBase(cert);
                        //rootC.setTrustStoreCertificates(listCertificates);
                        return  rootC;
                    })
                    .collect(Collectors.toList());
            certificateRepository.saveAll(listRootCertificates);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Certificate getCertificateForDateBase(java.security.cert.Certificate cert)  {
        try {
            X509Certificate x509Cert = (X509Certificate) cert;
            X500Name x500Name = new JcaX509CertificateHolder(x509Cert).getSubject();
            long serialNumber = x509Cert.getSerialNumber().longValue();
            String commonName = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.CN)[0].getFirst().getValue());
            String organizationalUnitName = IETFUtils.valueToString(x500Name.getRDNs(BCStyle.OU)[0].getFirst().getValue());
            return new Certificate(serialNumber, commonName, organizationalUnitName);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FtpApplication loadRootCA() {
        if(rootCA == null) {
            rootCA = ftpApplicationService.getFtpApplication(1L);
        }

        return rootCA;
    }



    @Override
    public CertificateSigningRequest prepareRootCaIssuerCSR() {
        int numOfAliases = 0;
        try {
            numOfAliases = keyStoreReaderService.getNumOfAliases(keyStore.getFile(), keyStorePassword);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CertificateSigningRequest csr = new CertificateSigningRequest("localhost", "MegaTravel",
                MainCompanyUnit+"_"+numOfAliases, "RS", UUID.randomUUID().toString(), null, null);
        return csr;
    }

    @Override
    public X509Certificate createCertificate(CertificateSigningRequest csr, CertificateType certificateType) throws Exception {
        //String str = Base64.encodeBase64String(generateKeyPair().getPublic().getEncoded());

        PublicKey publicKeyOfSubject;
        //PrivateKey privateKeyOfSubject = null;
        IssuerData issuerData;
        if(certificateType == CertificateType.OTHER) {
            //FtpApplication ftpRootCa = loadRootCA();
            //String alias = ftpRootCa.getOrganizationalUnitName();


            //issuerData = keyStoreReaderService.readIssuerFromStore(keyStorePath, alias, keyStorePassword.toCharArray(), keyStorePassword.toCharArray());
            X509Certificate issuerCertificate = (X509Certificate) keyStoreReaderService.readCertificate(keyStore.getFile(), keyStorePassword, MainRootCa);
            if(issuerCertificate == null || certificateExpiresFor(issuerCertificate) < OTHER_CERTIFICATE_DURATION *365 ) {
                // kada jos nije napravljen RootCA-ov sertifikat ili ako istice za manje od 2 godine
                // pravimo novi sertifikat za RootCA
                CertificateSigningRequest issuerCsr = prepareRootCaIssuerCSR();
                issuerCertificate = createRootCertificate();
            }

            issuerData = keyStoreReaderService.getIssuerData(keyStore.getFile(), MainRootCa, keyStorePassword, keyStorePassword, issuerCertificate);

            publicKeyOfSubject =  getPublicKey(csr.getPublicKey());
            applicationAddressService.setApplicationAddress(csr.getOrganizationalUnitName(), csr.getDestinationUrl());
        }
        else if (certificateType == CertificateType.ROOT) {
            KeyPair keyPair = generateKeyPair();
            publicKeyOfSubject = keyPair.getPublic(); // u ovom slucaju RootCA je i Subject i Issuer
            PrivateKey privateKeyOfIssuer = keyPair.getPrivate(); // u ovom slucaju RootCA je i Subject i Issuer
            issuerData = generateIssuerData(privateKeyOfIssuer, csr.getCommonName(),
                    csr.getOrganizationName(), csr.getOrganizationalUnitName(),csr.getCountryCode(), ""+csr.getUserId());
        }
        else {
            throw new Exception("Za sada CertificateTYpe ima vrednosti ROOT i OTHER");
        }

        SubjectData subjectData = generateSubjectData(csr, certificateType, publicKeyOfSubject);

        Certificate certificate = new Certificate(csr.getCommonName(), csr.getOrganizationalUnitName());
        certificateRepository.save(certificate);
        subjectData.setSerialNumber(""+certificate.getId());
        certificate.setSerialNumber(certificate.getId());
        // TODO trbe ovo srediti, i videti da li nam treba ovaj serialNumber
        certificateRepository.save(certificate);

        X509Certificate certificateX509 = certificateGeneratorService.generateCertificate(subjectData, issuerData);

        writeCertificateInFile(csr.getOrganizationalUnitName(), certificateX509);

        if(certificateType == CertificateType.OTHER) {
            //keyStoreWriterService.loadKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(trustStore.getFile(), trustStorePassword);
            keyStoreWriterService.writeCertificate(csr.getOrganizationalUnitName(), certificateX509);
            //keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(trustStore.getFile(), trustStorePassword);

            replaceTrustStoresOfOtherApplications(certificate);
        }
        else {
            //keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(keyStore.getFile(), keyStorePassword);
            //keyStoreWriterService.write(csr.getOrganizationalUnitName(), issuerData.getPrivateKey(), keyStorePassword.toCharArray(), certificateX509);
            keyStoreWriterService.write(MainRootCa, issuerData.getPrivateKey(), keyStorePassword, certificateX509);
            //keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(keyStore.getFile(), keyStorePassword);

            //keyStoreWriterService.loadKeyStore(trustStorePath, trustStorePassword.toCharArray());
            //keyStoreWriterService.loadKeyStore(trustStore.getFile(), trustStorePassword);
            //keyStoreWriterService.writeCertificate(csr.getOrganizationalUnitName(), certificateX509);
            //keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword.toCharArray());
            //keyStoreWriterService.saveKeyStore(trustStore.getFile(), trustStorePassword);
        }

        /*if (csr.getDestinationUrl() != null && !csr.getDestinationUrl().equals("")) {
            sendCertificateDestination(csr.getDestinationUrl(), certificateX509);
        }*/

        return certificateX509;
    }

    //@EventListener(ApplicationReadyEvent.class)
    @Override
    public X509Certificate createRootCertificate() throws Exception {
        CertificateSigningRequest rootCsr = prepareRootCaIssuerCSR();
        X509Certificate rootCertificate = createCertificate(rootCsr, CertificateType.ROOT);
        return rootCertificate;
    }

    private void writeCertificateInFile(String commonName, X509Certificate certificate) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(directoryStoresPath + "/" + commonName + ".cer");
            out.write(certificate.getEncoded());
            out.close();
        } catch (CertificateEncodingException|IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isRevokedById(Long certificateId) throws Exception {
        Certificate certificate = certificateRepository.findById(certificateId).orElseThrow(
                () -> new Exception(String.format("Certificate with id %d is revoked!", certificateId.longValue())));
        return certificate.isRevoked();
    }

    @Override
    public boolean isRevoked(Long serialNumber) throws Exception {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber);
        if(certificate == null) {
            throw new Exception("Certificate does not exist");
        }
        return certificate.isRevoked();
    }

    @Override
    public void revoke(long serialNumber) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber);
        certificate.setRevoked(true);
        certificateRepository.save(certificate);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=true, noRollbackFor=Exception.class)
    @Override
    public List<Certificate> getNonRevokedCertiificates() {
        List<Certificate> certificates = certificateRepository.findDistinctByRevoked(false);
        certificates.stream()
                .forEach(cert -> cert.setTrustStoreCertificates(
                        cert.getTrustStoreCertificates().stream()
                                .filter(c -> !c.isRevoked())
                                .collect(Collectors.toList())
                    )
                );
        return certificates;
    }

    @Override
    public void saveCertificate(X509Certificate certificate) throws CertificateEncodingException {
        String commonName = getCommonName(certificate);
        Certificate dbCertificate = new Certificate()
        {{
           setRevoked(false);
           setCommonName(commonName);
           setSerialNumber(certificate.getSerialNumber().longValue());
        }};
        certificateRepository.save(dbCertificate);
    }

    @Override
    public void saveCertificate(Certificate certificate) {
        certificateRepository.save(certificate);
    }

    @Override
    public File prepareTrustStoreFile(String organizationalUnitName, List<String> trustStoreCertificateOrganizationalUnitNames, List<java.security.cert.Certificate> trustStoreCertificates) throws Exception {
        if(trustStoreCertificates == null) {
            trustStoreCertificates = keyStoreReaderService.readCertificates(trustStore.getFile(), trustStorePassword, trustStoreCertificateOrganizationalUnitNames, true);
            List<java.security.cert.Certificate> rootCertificates = keyStoreReaderService.readCertificates(keyStore.getFile(), keyStorePassword, null, false);
            trustStoreCertificates.addAll(rootCertificates); // stavicemo i sve root sertifikate
            List<String> rootCertificateOrganizationalUnitNames = rootCertificates.stream()
                    .map(cert -> getOrganizationalUnitName(cert))
                    .collect(Collectors.toList());
            trustStoreCertificateOrganizationalUnitNames.addAll(rootCertificateOrganizationalUnitNames);
            // sve ovi sertifikati ce biti poslani u trustStore fajlu preko FTP
        }
        else{
            if(trustStoreCertificateOrganizationalUnitNames == null) {
                trustStoreCertificateOrganizationalUnitNames = trustStoreCertificates.stream()
                        .map(cert -> getOrganizationalUnitName(cert))
                        .collect(Collectors.toList());
            }
            else {
                if (trustStoreCertificateOrganizationalUnitNames.size() != trustStoreCertificates.size()) {
                    throw new Exception("TrustStoreCertificateOrganizationalUnitNames i trustStoreCertificates moraju biti iste duzine");
                }

                for(int i = 0; i < trustStoreCertificates.size(); i++) {
                    if(!getOrganizationalUnitName(trustStoreCertificates.get(i)).equalsIgnoreCase(trustStoreCertificateOrganizationalUnitNames.get(i))) {
                        throw new Exception("Na poziciji (indeksu) " + i + " ne poklapaju se organizationalUnitName.");
                    }
                }
            }

        }

        char[] password = "trust_store_pass".toCharArray();
        String newTrustStoreFilePath = directoryTempPath+"truststore_"+organizationalUnitName+".jks";
        //newTrustStoreFilePath = newTrustStoreFilePath.substring(1);

        File trustStoreFile = new File(newTrustStoreFilePath);

        keyStoreWriterService.writeCertificates(trustStoreFile, password, trustStoreCertificateOrganizationalUnitNames, trustStoreCertificates);

        if(!trustStoreFile.exists()) {
            throw new Exception("Nije pronadjen novi truststore, a mora bi postojati");
        }
        else {
            /*boolean success = trustStoreFile.delete();
            if(!success) {
                throw new Exception("Problem sa brisanjem novog truststore-a ("+newTrustStoreFilePath+")!");
            }*/

            return trustStoreFile;
        }
    }

    @Override
    public SubjectData generateSubjectData(CertificateSigningRequest csr, CertificateType certificateType, PublicKey publicKey) {
        LocalDateTime startLocalDateTime = LocalDateTime.now();
        //LocalDateTime startLocalDateTime = LocalDateTime.now().minusYears(ROOT_CERTIFICATE_DURATION).plusDays(10);

        long years;
        if(certificateType == CertificateType.ROOT) years = ROOT_CERTIFICATE_DURATION;
        //else if(certificateType == CertificateType.INTERMEDIATE) years = INTERMEDIATE_CERTIFICATE_DURATION;
        else years = OTHER_CERTIFICATE_DURATION;

        LocalDateTime endLocalDateTime = startLocalDateTime.plusYears(years);
        //LocalDate endLocalDate = startLocalDate.plusDays(2);
        Date startDate = asDate(startLocalDateTime);
        Date endDate = asDate(endLocalDateTime);

        //Serijski broj sertifikata
        String sn="1";
        //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, csr.getCommonName());
        //builder.addRDN(BCStyle.SURNAME, csr.getSurname());
        //builder.addRDN(BCStyle.GIVENNAME, csr.getGivenName());
        builder.addRDN(BCStyle.O, csr.getOrganizationName());
        builder.addRDN(BCStyle.OU, csr.getOrganizationalUnitName());
        builder.addRDN(BCStyle.C, csr.getCountryCode());
        //builder.addRDN(BCStyle.E, csr.getEmailAddress());
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, csr.getUserId());

        //Kreiraju se podaci za sertifikat, sto ukljucuje:
        // - javni kljuc koji se vezuje za sertifikat
        // - podatke o vlasniku
        // - serijski broj sertifikata
        // - od kada do kada vazi sertifikat
        return new SubjectData(publicKey, builder.build(), startDate, endDate);
    }

    @Override
    public IssuerData generateIssuerData(PrivateKey issuerKey, String commonName, String organizationName,
                                         String organizationalUnitName, String countryCode, String userId) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, commonName);
        //builder.addRDN(BCStyle.SURNAME, "Luburic");
        //builder.addRDN(BCStyle.GIVENNAME, "Nikola");
        builder.addRDN(BCStyle.O, organizationName);
        builder.addRDN(BCStyle.OU, organizationalUnitName);
        builder.addRDN(BCStyle.C, countryCode);
        //builder.addRDN(BCStyle.E, "nikola.luburic@uns.ac.rs");
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, userId);

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, builder.build());
    }

    private String getCommonName(X509Certificate certificate) throws CertificateEncodingException {
        X500Name x500name = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        return IETFUtils.valueToString(cn.getFirst().getValue());
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private PublicKey getPublicKey(String publicKey){
        try{
            byte[] publicKeyBytes = Base64.decodeBase64(publicKey);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public void sendFile(File trustStoreFile, String organizationalUnitName) throws Exception {
        FtpApplication ftpApplication = ftpApplicationService.getFtpApplication(organizationalUnitName);
        if(ftpApplication != null) {
            defaultFtpSessionFactory.setHost(ftpApplication.getHost());
            defaultFtpSessionFactory.setPort(ftpApplication.getPort());
            defaultFtpSessionFactory.setUsername(ftpApplication.getUsername());
            defaultFtpSessionFactory.setPassword(ftpApplication.getPassword());
            gateway.sendToFtp(trustStoreFile);
        }
        else {
            throw new Exception("Nije pronadjena aplikacija sa organizationalUnitName: " + organizationalUnitName);
        }

    }

    @Override
    public long certificateExpiresFor(X509Certificate certificate) {
        Date date = certificate.getNotAfter();
        long difference = TimeUnit.DAYS.convert(date.getTime() - new Date().getTime(), TimeUnit.MILLISECONDS);
        System.out.println("certificateExpiresFor(): " + difference + " (DAYS)");
        return difference;
    }

    @Override
    public ChangedTrustStoreConfig isChangedTrustStoreConfig(TrustStoreConfigDTO trustStoreConfig, List<Certificate> nonRevokedCertificates) {
        Certificate certificate = nonRevokedCertificates.stream()
                .filter(cert -> cert.getOrganizationalUnitName().equals(trustStoreConfig.getOrganizationalUnitName()))
                .findFirst().get();

        int firstSize = certificate.getTrustStoreCertificates().size();

        List<Certificate> newTrustStoreCertificates = new ArrayList<Certificate>();
        List<String> copyTrustStoreCertificateOrganizationalUnitNames = new ArrayList<String>();
        copyTrustStoreCertificateOrganizationalUnitNames.addAll(trustStoreConfig.getTrustStoreCertificateOrganizationalUnitNames());
        String currentOrganizationalUnitName;

        for (Certificate cert : certificate.getTrustStoreCertificates()) {
            if((currentOrganizationalUnitName = getOrganizationalUnitName(cert,
                    copyTrustStoreCertificateOrganizationalUnitNames)) != null) {
                newTrustStoreCertificates.add(cert);
                copyTrustStoreCertificateOrganizationalUnitNames.remove(currentOrganizationalUnitName);
            }
        }

        int secondSize = newTrustStoreCertificates.size();


        for (String organizationalUnitName : copyTrustStoreCertificateOrganizationalUnitNames) {
            newTrustStoreCertificates.add(getCertificate(organizationalUnitName, nonRevokedCertificates));
        }

        int thirdSize = newTrustStoreCertificates.size();

        if(firstSize == secondSize && secondSize == thirdSize) {
            // sva tri su jednaka, nista se nije promenilo
            return new ChangedTrustStoreConfig(null, false);
        }
        else {
            certificate.setTrustStoreCertificates(newTrustStoreCertificates);
            return new ChangedTrustStoreConfig(certificate, true);
        }

    }

    @Override
    public List<Certificate> getTrustStoreCertificates(String organizationalUnitName) {
        return certificateRepository.findByOrganizationalUnitName(organizationalUnitName).getTrustStoreCertificates();
    }

    @Override
    public String getOrganizationalUnitName(java.security.cert.Certificate certificate) {
        return keyStoreWriterService.getOrganizationalUnitName(certificate);
    }

    private Certificate getCertificate(String organizationalUnitName, List<Certificate> nonRevokedCertificates) {
        return nonRevokedCertificates.stream()
                .filter(cert -> cert.getOrganizationalUnitName().equals(organizationalUnitName))
                .findFirst().orElseGet(null);
    }

    private String getOrganizationalUnitName(Certificate certificate, List<String> organizationalUnitNames) {
        return organizationalUnitNames.stream()
                .filter(oun -> oun.equals(certificate.getOrganizationalUnitName()))
                .findFirst().orElse(null);
    }

    public void replaceTrustStoresOfOtherApplications(Certificate newCertificate) {
        try {
            List<java.security.cert.Certificate> rootCertificates = keyStoreReaderService.readCertificates(keyStore.getFile(), keyStorePassword, null, false);
            List<java.security.cert.Certificate> certificates = keyStoreReaderService.readCertificates(trustStore.getFile(), trustStorePassword, null, true);


            List<com.bsep_sbz.PKI.model.Certificate> nonRevokedCertificates = getNonRevokedCertiificates();
            nonRevokedCertificates.stream()
                    // necemo menjati trustStore PKI aplikacije i aplikacije za koju je upravo napravljen novi sertifikat,
                    // pa zbog toga i menjamo trustStore-ove drugim aplikacijam
                    .filter(cer -> !cer.getOrganizationalUnitName().equalsIgnoreCase(newCertificate.getOrganizationalUnitName())
                           && !cer.getOrganizationalUnitName().toLowerCase().startsWith(MainCompanyUnit.toLowerCase()))
                    .filter(cert -> containsOrganizationalUnitNameInTrustStoreCertificates(cert.getTrustStoreCertificates(), newCertificate.getOrganizationalUnitName()))
                    .forEach(cert -> {
                        changeTrustStoreCertificates(cert, newCertificate);
                        
                        List<java.security.cert.Certificate> newTrustStoreCertificates = new ArrayList<java.security.cert.Certificate>();
                        List<java.security.cert.Certificate> filteredTrustStoreCertificates = filterCertificates(certificates, cert.getTrustStoreCertificates());
                        newTrustStoreCertificates.addAll(filteredTrustStoreCertificates);
                        newTrustStoreCertificates.addAll(rootCertificates);

                        File trustStoreFile = null;
                        try {
                            trustStoreFile = prepareTrustStoreFile(cert.getOrganizationalUnitName(), null, newTrustStoreCertificates);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            sendFile(trustStoreFile, cert.getOrganizationalUnitName());
                            System.out.println("Poslao "+cert.getOrganizationalUnitName()+"-u njegov truststore.");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeTrustStoreCertificates(Certificate cert, Certificate newCertificateForTrustStore) {
        List<Certificate> newTrustStoreCertificates = cert.getTrustStoreCertificates().stream().
                filter(c -> !c.getOrganizationalUnitName().equalsIgnoreCase(newCertificateForTrustStore.getOrganizationalUnitName()))
                .collect(Collectors.toList());
        newTrustStoreCertificates.add(newCertificateForTrustStore);
        cert.setTrustStoreCertificates(newTrustStoreCertificates);
        certificateRepository.save(cert);
    }

    private boolean containsOrganizationalUnitNameInTrustStoreCertificates(List<Certificate> trustStoreCertificates, String organizationalUnitName) {
        for(Certificate certificate : trustStoreCertificates) {
            if(certificate.getOrganizationalUnitName().equalsIgnoreCase(organizationalUnitName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<java.security.cert.Certificate> filterCertificates(List<java.security.cert.Certificate> certificates, List<com.bsep_sbz.PKI.model.Certificate> certificatesDB) {
        List<String> organizitionalUnitNames = certificatesDB.stream()
                .map(cert -> cert.getOrganizationalUnitName())
                .collect(Collectors.toList());
        return certificates.stream()
                .filter(cert -> {
                    String organizitionalUnitName = getOrganizationalUnitName(cert);
                    return organizitionalUnitNames.contains(organizitionalUnitName);
                })
                .collect(Collectors.toList());
    }

}
