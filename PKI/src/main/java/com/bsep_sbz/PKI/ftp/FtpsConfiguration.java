package com.bsep_sbz.PKI.ftp;

import org.apache.commons.net.util.KeyManagerUtils;
import org.apache.commons.net.util.TrustManagerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.Message;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Configuration
public class FtpsConfiguration {

    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${server.ssl.key-alias}")
    private String keyAlias;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private char[] trustStorePassword;

    @Value("${ftp.target-directory-name}")
    private String ftpTargetDirectoryName;

    /*@Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.port}")
    private int ftpPort;

    @Value("${ftp.username}")
    private String ftpUsername;

    @Value("${ftp.password}")
    private String ftpPassword;*/



    /*@Bean
    public CachingSessionFactory<FTPFile> defaultFtpSessionFactory() throws IOException, GeneralSecurityException {
        DefaultFtpSessionFactory dfsf = new DefaultFtpSessionFactory();

        dfsf.setHost(ftpHost);
        dfsf.setPort(ftpPort);
        dfsf.setUsername(ftpUsername);
        dfsf.setPassword(ftpPassword);
        //dfsf.setClientMode(1);
        //dfsf.setFileType(2);

        return new CachingSessionFactory<FTPFile>(dfsf);
    }*/


    @Bean
    public DefaultFtpSessionFactory defaultFtpSessionFactory() {
        DefaultFtpSessionFactory dfsf = new DefaultFtpSessionFactory();

        /*dfsf.setHost(ftpHost);
        dfsf.setPort(ftpPort);
        dfsf.setUsername(ftpUsername);
        dfsf.setPassword(ftpPassword);*/
        //dfsf.setClientMode(1);
        dfsf.setFileType(2);

        return dfsf;
    }


    @Bean
    public FtpsSessionFactory ftpsSessionFactory() throws IOException, GeneralSecurityException {
        FtpsSessionFactory fsf = new FtpsSessionFactory();

        /*fsf.setHost(ftpHost);
        fsf.setPort(ftpPort);
        fsf.setUsername(ftpUsername);
        fsf.setPassword(ftpPassword);*/
        //dfsf.setClientMode(1);
        fsf.setFileType(2);
        fsf.setUseClientMode(true);
        fsf.setImplicit(true);
        fsf.setProt("P");
        fsf.setProtocol("TLSv1.2");
        fsf.setProtocols(new String[]{"TLSv1.2"});
        fsf.setNeedClientAuth(true);
        fsf.setKeyManager(KeyManagerUtils.createClientKeyManager(keyStore.getFile(), keyStorePassword, keyAlias));

        KeyStore ts = KeyStore.getInstance("JKS");
        ts.load(new FileInputStream(trustStore.getFile()), trustStorePassword);
        fsf.setTrustManager(TrustManagerUtils.getDefaultTrustManager(ts));

        return fsf;
    }

    @Bean
    public IntegrationFlow ftpOutboundFlow() throws IOException, GeneralSecurityException {
        return IntegrationFlows.from("toFtpChannel")
                .handle(Ftp.outboundAdapter(defaultFtpSessionFactory(), FileExistsMode.REPLACE)
                //.handle(Ftp.outboundAdapter(ftpsSessionFactory(), FileExistsMode.FAIL)
                        .useTemporaryFileName(false)
                        //.fileNameExpression("headers['" + FileHeaders.FILENAME + "']")
                        .fileNameGenerator(new FileNameGenerator() {
                            @Override
                            public String generateFileName(Message<?> message) {
                                return "truststore.jks";
                            }
                        })
                        .remoteDirectory(ftpTargetDirectoryName)

                ).get();
    }

    /*private static final class SharedSSLFTPSClient extends FTPSClient {

        @Override
        protected void _prepareDataSocket_(final Socket socket) throws IOException {
            if (socket instanceof SSLSocket) {
                // Control socket is SSL
                final SSLSession session = ((SSLSocket) _socket_).getSession();
                final SSLSessionContext context = session.getSessionContext();
                context.setSessionCacheSize(0); // you might want to limit the cache
                try {
                    final Field sessionHostPortCache = context.getClass()
                                                                .getDeclaredField("sessionHostPortCache");
                    sessionHostPortCache.setAccessible(true);
                    final Object cache = sessionHostPortCache.get(context);

                    final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    method.setAccessible(true);

                    String key = String.format("%s:%s", socket.getInetAddress().getHostName(),
                                                String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                    method.invoke(cache, key, session);

                    key = String.format("%s:%s", socket.getInetAddress().getHostAddress(),
                                                String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT);
                    method.invoke(cache, key, session);
                }
                catch (NoSuchFieldException e) {
                    // Not running in expected JRE
                    System.out.println("No field sessionHostPortCache in SSLSessionContext");
                    e.printStackTrace();
                }
                catch (Exception e) {
                    // Not running in expected JRE
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }

        }

    }*/
}
