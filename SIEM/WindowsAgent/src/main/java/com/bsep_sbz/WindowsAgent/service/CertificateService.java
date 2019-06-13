package com.bsep_sbz.WindowsAgent.service;

import com.bsep_sbz.WindowsAgent.controller.dto.CertificateSigningRequest;
import com.bsep_sbz.WindowsAgent.service.interfaces.ICertificateService;
import com.bsep_sbz.WindowsAgent.service.keystore.KeyStoreReaderService;
import com.bsep_sbz.WindowsAgent.service.keystore.KeyStoreWriterService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.util.UUID;


@Service
public class CertificateService implements ICertificateService
{
    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private KeyStoreWriterService keyStoreWriterService;

    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private char[] keyStorePassword;

    @Value("${server.ssl.key-alias}")
    private String aliasKeyStore;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;

    @Value("${my-url}")
    private String myUrl;

    private final String directoryClassesPath = this.getClass().getResource("../../../../").getPath(); // target/classes folder
    private String directoryStoresPath = directoryClassesPath + "stores"; // target/classes/stores folder


    @Override
    public CertificateSigningRequest prepareCSR(PublicKey publicKey) {
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        CertificateSigningRequest csr = new CertificateSigningRequest("localhost", "MegaTravel",
                "MegaTravelSiemWindowsAgent", "RS", UUID.randomUUID().toString(), publicKeyStr, myUrl);
        return csr;
    }

    /*@Override
    public CertificateSigningRequest prepareCSR(PublicKey publicKey) {
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        CertificateSigningRequest csr = new CertificateSigningRequest("localhost", "MegaTravel",
                "MegaTravelClient", "RS", UUID.randomUUID().toString(), publicKeyStr, "https://localhost:4200");
        return csr;
    }*/

    @Override
    public void saveCertificate(PrivateKey privateKey, X509Certificate certificate) throws IOException {
        writeCertificateInFile(aliasKeyStore, certificate);

        //writePrivateKeyInFile("localhost", privateKey);
        //writeCertificateInFile("MegaTravelClient", certificate);

        keyStoreWriterService.loadKeyStore(keyStore.getFile(), keyStorePassword);
        keyStoreWriterService.write(aliasKeyStore, privateKey, keyStorePassword, certificate);
        keyStoreWriterService.saveKeyStore(keyStore.getFile(), keyStorePassword);
    }

    @Override
    public void saveCertificateInTrustStore(X509Certificate certificate) throws IOException {
        writeCertificateInFile("test", certificate);
        keyStoreWriterService.loadKeyStore(trustStore.getFile(), trustStorePassword);
        keyStoreWriterService.writeCertificate("test", certificate);
        keyStoreWriterService.saveKeyStore(trustStore.getFile(), trustStorePassword);
    }

    @Override
    public  X509Certificate getCertificate(String certificateStr) {
        byte[] bytes = Base64.decodeBase64(certificateStr);

        try {
            //return new JDKX509CertificateFactory().engineGenerateCertificate(inputStream);
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(bytes);
            X509Certificate certificate = (X509Certificate)certFactory.generateCertificate(in);
            return certificate;
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadCertificate(String certificatePath) {
        /*Certificate certificate = null;
        try{
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            certificate = cf.generateCertificate(new FileInputStream(certificatePath));
            System.out.println(certificate);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        String privateKeyStr = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDcmDjclSgSfb+71eQ0SlExR//KPipsui2nGDOmWqkfbVAvnm0Gm6ZDoAyrVjGAn/HiJjwgE/7ZPf60wnX/1Sd5Ps2LglrhOFCLkVegWehuc35k0VuPjMOhCy6EpoteFr/C+lOQWT+jhagOP5peH+N4w6va76t7deXkS6M4plNjJ5lPysn1qXxOjEqfrVtiyMAPy+DujZiR8BO/JYeuwGMfBR7MK4q9SCnQxmhyBU6DD/iZOxE6D4Utk3sbm/Kh6jT5LzSJiqr7JyYv0UDtT3E9/4AWv/MYYxwNzgFXbdp0IGWnoFamjCBgfiCNUvMD80JJB7QD9ar5MrKW4jc5m3y/AgMBAAECggEADRVeMf8KvGYG7C4Ydd1+iUy66BTd0UsNkB6IWbKA22NRYTGlR8INj287sSHQl89kukQIRuLIt7vMko4CD6VlzPQjnsWhfEApzs1VBCaw+XQSKAqewpnXtIKERMvX8kGpJ/hO4n3Yn2s693VgQ3ris/PvCnIymoT4mJy/1olO4s+HQHKfPVHEy3I2h50FlYQ6FJifRx1jaSaXXoKAFiho33+pcd8woECfFMWAOIa5BQ1UkRKuOKHBAgKBmtC9QrrO/wVi3bVPnewYIYym4WOGj7Zk/nFVxYAB9Ezpq67iBnYStyDLr9YtydQ2TV0SYhye7TwwISoYEMwswMqoO/h9AQKBgQDwObQBuUCL5r+8+ri+3wuBuRhmWeNS9j2WCUUeCu8XYUq6MQ/5/ItPnj9SSARgnbmyGTH/tZG9R8qciMWzNFTz19WHjb+2cJwVAVgClkvWQeiIqHVYq1CfA/p0EqoVYO0m+xYH+AXrL8k+XmxKD4ijeeOdkquyCpDWEIcvPv1jPwKBgQDrFIRDVU3+ngBz+F1REZ2Gd7eBLyFQLR4B0fEaGlV52XxsA1K5SznmSjL7r7RMKDpxBP3aTkorfowbAywf+0911HdBKeIuvx51pHG1BJiE+LvVuI96sS/ENp7MpzCXNqBuFJD4sSriRXPe2i4A7q+0nlSrMsXGbJuRgwkZUycGgQKBgQCzLEa/dl3XOx9No6+B5bieY+D4DhlIepMKwhVESCF2TcG/x/JDoYKCuuQ9yatCKMIlPTeWouV1yQMs57KVa/Xk/dmsxiP0vl8cJ6luHI2p2/TFYOhNX1G9mOkIBBfJ5EZd6a7P+MTpWvX9x817G8TtMW98yrW3ELxgfWlm6tNxXwKBgENPS5OwADYQbZbHHYkR3yXab3uY0BQR5w6kW0GbGrBZ+os+5FfT108+HZz9MUXBGwKzaDAyzH/pqxos7Vx268iUPlU1NeMmrAUJQdmP6E/inH3Ua5i41TF8Zjm5AWu6o3MBhoT+zYWeV1AO7q8rqLmxwBKI5iIrhNvERj2vFnSBAoGBAJ/H3+3UnDu2VJupM1hLYztXtWjeD/ytN7nRm7+P35KLnm5JcZUpE5fWXwoctppYjbPuNpjujTBrraBvhMlt871/SC1+fwSFHLkrZZHmOl4qZaj6gCXvk4vlq9Fea2AXWApKdPwIjX+I8qyx6C0rhF0aFVQYSiiQclh+5LCs46Cm";
        //PrivateKey privateKey = getPrivateKey(privateKeyStr);
        keyStoreWriterService.loadKeyStore(null, "key_store_pass".toCharArray());
        keyStoreWriterService.write("megatravelsiemcenter", privateKey, "key_store_pass".toCharArray(), certificate);
        keyStoreWriterService.saveKeyStore("./src/main/resources/stores/keystore.jks", "key_store_pass".toCharArray());
        writeCertificateInFile((X509Certificate) certificate);*/
    }

    @Override
    public KeyPair generateKeyPair() {
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

    private void writeCertificateInFile(String filname, X509Certificate certificate) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(directoryStoresPath+"/" +filname+".cer");
            out.write(certificate.getEncoded());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writePrivateKeyInFile(String filename, PrivateKey privateKey) {
        PemWriter pemWriter = null;
        try {
            pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(directoryStoresPath+"/" +filename+".key.pem")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            pemWriter.writeObject(new PemObject("RSA PRIVATE KEY", privateKey.getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                pemWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
