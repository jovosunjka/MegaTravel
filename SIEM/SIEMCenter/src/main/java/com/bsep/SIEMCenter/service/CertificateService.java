package com.bsep.SIEMCenter.service;

import com.bsep.SIEMCenter.controller.dto.CertificateSigningRequest;
import com.bsep.SIEMCenter.service.interfaces.ICertificateService;
import com.bsep.SIEMCenter.service.keystore.KeyStoreReaderService;
import com.bsep.SIEMCenter.service.keystore.KeyStoreWriterService;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;


@Service
public class CertificateService implements ICertificateService
{
    @Autowired
    private KeyStoreReaderService keyStoreReaderService;

    @Autowired
    private KeyStoreWriterService keyStoreWriterService;

    //@Value("${server.ssl.key-store}")
    private String keyStorePath;

    //@Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    //@Value("${server.ssl.key-alias}")
    private String aliasKeyStore = "megatravelsiemcenter";

    //@Value("${trust-store-password}")
    private String trustStorePassword;

    @Value("${my-url}")
    private String myUrl;

    @Override
    public CertificateSigningRequest prepareCSR(PublicKey publicKey) {
        String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
        CertificateSigningRequest csr = new CertificateSigningRequest("localhost", "MegaTravel",
                "MegaTravelSiemCenter22222", "RS", "61897", publicKeyStr, myUrl);
        return csr;
    }

    @Override
    public void saveCertificate(PrivateKey privateKey, X509Certificate certificate) {
        writeCertificateInFile(certificate);
        keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        keyStoreWriterService.write(aliasKeyStore, privateKey, keyStorePassword.toCharArray(), certificate);
        keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
    }

    @Override
    public void saveCertificateInTrustStore(X509Certificate certificate) {
        writeCertificateInFile(certificate);
        keyStoreWriterService.loadKeyStore(keyStorePath, keyStorePassword.toCharArray());
        keyStoreWriterService.writeCertificateInTrustStore(certificate.getSubjectX500Principal().getName(),
                trustStorePassword.toCharArray(), certificate);
        keyStoreWriterService.saveKeyStore(keyStorePath, keyStorePassword.toCharArray());
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

    private void writeCertificateInFile(X509Certificate certificate) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("./src/main/resources/stores/" +aliasKeyStore+".cer");
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


}
