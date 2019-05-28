package com.bsep_sbz.PKI.service;

import com.bsep_sbz.PKI.dto.CertificateSigningRequest;
import com.bsep_sbz.PKI.model.*;
import com.bsep_sbz.PKI.model.Certificate;
import com.bsep_sbz.PKI.repository.CertificateRepository;
import com.bsep_sbz.PKI.service.certificate.CertificateGeneratorService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreReaderService;
import com.bsep_sbz.PKI.service.keystore.KeyStoreWriterService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private KeyStoreWriterService keyStoreWriterService;

    @Autowired
    private CertificateGeneratorService certificateGeneratorService;

    @Autowired
    private UserService userService;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private RestTemplate restTemplate;


    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private char[] keyStorePassword;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;


    private final String directoryClassesPath = this.getClass().getResource("../../../../").getPath(); // target/classes folder
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

    @Override
    public X509Certificate createCertificate(CertificateSigningRequest csr) throws Exception {
        //String str = Base64.encodeBase64String(generateKeyPair().getPublic().getEncoded());
        User admin = userService.getLoggedUser();
        CA ca = admin.getCa();
        String alias = ca.getOrganizationalUnitName();

        PublicKey publicKeyOfSubject;
        //PrivateKey privateKeyOfSubject = null;
        IssuerData issuerData;
        if(csr.getCertificateType() == CertificateType.OTHER) {
            //issuerData = keyStoreReaderService.readIssuerFromStore(keyStorePath, alias, keyStorePassword.toCharArray(), keyStorePassword.toCharArray());
            issuerData = keyStoreReaderService.readIssuerFromStore(keyStore.getFile(), alias, keyStorePassword, keyStorePassword);
            if(issuerData == null) {
                throw new Exception("Prvo mora biti napravljen CA-ov sertifikat!");
            }

            publicKeyOfSubject =  getPublicKey(csr.getPublicKey());
            //KeyPair keyPair = generateKeyPair();
            //publicKeyOfSubject = keyPair.getPublic();
            //privateKeyOfSubject = keyPair.getPrivate();
            //String privateKeyOfSubjectstr = Base64.encodeBase64String(privateKeyOfSubject.getEncoded());
            //System.out.println();
        }
        else {
            KeyPair keyPair = generateKeyPair();
            publicKeyOfSubject = keyPair.getPublic(); // u ovom slucaju RootCA je i Subject i Issuer
            PrivateKey privateKeyOfIssuer = keyPair.getPrivate(); // u ovom slucaju RootCA je i Subject i Issuer
            issuerData = generateIssuerData(privateKeyOfIssuer, csr.getCommonName(),
                    csr.getOrganizationName(), csr.getOrganizationalUnitName(),csr.getCountryCode(), ""+csr.getUserId());
        }
        SubjectData subjectData = generateSubjectData(csr, publicKeyOfSubject);

        Certificate certificate = new Certificate(csr.getCommonName());
        certificateRepository.save(certificate);
        subjectData.setSerialNumber(""+certificate.getId());

        X509Certificate certificateX509 = certificateGeneratorService.generateCertificate(subjectData, issuerData);

        writeCertificateInFile(csr.getOrganizationalUnitName(), certificateX509);

        if(csr.getCertificateType() == CertificateType.OTHER) {
            //keyStoreWriterService.loadKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(trustStore.getFile(), trustStorePassword);
            keyStoreWriterService.writeCertificate(csr.getOrganizationalUnitName(), certificateX509);
            //keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(trustStore.getFile(), trustStorePassword);


            /*FileOutputStream out = null;
            try {
                out = new FileOutputStream(directoryStoresPath + "/localhost.key");
                out.write(privateKeyOfSubject.getEncoded());
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }*/

            /*
            keyStoreWriterService.loadKeyStore(null, keyStorePassword.toCharArray());
            //keyStoreWriterService.loadKeyStore(keyStore.getFile(), keyStorePassword);
            keyStoreWriterService.write(csr.getOrganizationalUnitName(), privateKeyOfSubject, keyStorePassword.toCharArray(), certificateX509);
            //keyStoreWriterService.write(alias, issuerData.getPrivateKey(), keyStorePassword, certificateX509);
            keyStoreWriterService.saveKeyStore(keyStorePath2, keyStorePassword.toCharArray());
            //keyStoreWriterService.saveKeyStore(keyStore.getFile(), keyStorePassword);*/
        }
        else {
            //keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(keyStore.getFile(), keyStorePassword);
            //keyStoreWriterService.write(csr.getOrganizationalUnitName(), issuerData.getPrivateKey(), keyStorePassword.toCharArray(), certificateX509);
            keyStoreWriterService.write(alias, issuerData.getPrivateKey(), keyStorePassword, certificateX509);
            //keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(keyStore.getFile(), keyStorePassword);

            //keyStoreWriterService.loadKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.loadKeyStore(trustStore.getFile(), trustStorePassword);
            keyStoreWriterService.writeCertificate(csr.getOrganizationalUnitName(), certificateX509);
            //keyStoreWriterService.saveKeyStore(trustStorePath, trustStorePassword.toCharArray());
            keyStoreWriterService.saveKeyStore(trustStore.getFile(), trustStorePassword);
        }

        /*if (csr.getDestinationUrl() != null && !csr.getDestinationUrl().equals("")) {
            sendCertificateDestination(csr.getDestinationUrl(), certificateX509);
        }*/

        return certificateX509;
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

    @Override
    public SubjectData generateSubjectData(CertificateSigningRequest csr, PublicKey publicKey) {
        LocalDate startLocalDate = LocalDate.now();

        long years;
        if(csr.getCertificateType() == CertificateType.ROOT) years = ROOT_CERTIFICATE_DURATION;
        else if(csr.getCertificateType() == CertificateType.INTERMEDIATE) years = INTERMEDIATE_CERTIFICATE_DURATION;
        else years = OTHER_CERTIFICATE_DURATION;

        LocalDate endLocalDate = startLocalDate.plusYears(years);
        Date startDate = asDate(startLocalDate);
        Date endDate = asDate(endLocalDate);

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

    private Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
