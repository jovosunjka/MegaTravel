package com.bsep_sbz.WindowsAgent.ftp;


import com.bsep_sbz.WindowsAgent.ftp.dto.FTPInfo;
import com.bsep_sbz.WindowsAgent.ftp.dto.UserInfo;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Component
public class MyFtpServer {
    private FtpServer ftpServer;
    private UserManager userManager;

    private static final int MAX_IDLE_TIME = 300;


    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.passive-ports}")
    private String passivePorts;

    @Value("${ftp.max-login}")
    private Integer maxLogin;

    @Value("${ftp.max-threads}")
    private Integer maxThreads;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.home-dir}")
    private Resource homeDir;

    @Value("${server.ssl.key-store}")
    private Resource keyStore;

    @Value("${server.ssl.key-store-password}")
    private String keyStorePassword;

    @Value("${server.ssl.key-alias}")
    private String keyAlias;

    @Value("${server.ssl.key-store-type}")
    private String keyStoreType;

    @Value("${server.ssl.trust-store}")
    private Resource trustStore;

    @Value("${server.ssl.trust-store-password}")
    private String trustStorePassword;

    @Value("${server.ssl.trust-store-type}")
    private String trustStoreType;


    @PostConstruct
    private void start() {
        try {
            mkHomeDir(homeDir.getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            createConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        FtpServerFactory serverFactory = new FtpServerFactory();

        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        connectionConfigFactory.setMaxLogins(maxLogin);
        connectionConfigFactory.setMaxThreads(maxThreads);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();

        listenerFactory.setPort(port);

        // define SSL configuration
        /*SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setSslProtocol("TLSv1.2");
        ssl.setClientAuthentication("NEED");

        try { ssl.setKeystoreFile(keyStore.getFile()); }
        catch (IOException e) { e.printStackTrace(); }
        ssl.setKeystorePassword(keyStorePassword);
        ssl.setKeyAlias(keyAlias);
        ssl.setKeystoreType(keyStoreType);

        try { ssl.setTruststoreFile(trustStore.getFile()); }
        catch (IOException e) { e.printStackTrace(); }
        ssl.setTruststorePassword(trustStorePassword);
        ssl.setTruststoreType(trustStoreType);

        // set the SSL configuration for the listener
        listenerFactory.setSslConfiguration(ssl.createSslConfiguration());
        listenerFactory.setImplicitSsl(true);

        DataConnectionConfigurationFactory dataConfigFactory = new DataConnectionConfigurationFactory();
        dataConfigFactory.setImplicitSsl(true);

        listenerFactory.setDataConnectionConfiguration(dataConfigFactory.createDataConnectionConfiguration());*/

        if (!Objects.equals(passivePorts, "")) {
            DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();

            dataConnectionConfFactory.setPassivePorts(passivePorts);
            if (!(Objects.equals(host, "localhost") || Objects.equals(host, "127.0.0.1"))) {

                dataConnectionConfFactory.setPassiveExternalAddress(host);
            }
            listenerFactory.setDataConnectionConfiguration(
                    dataConnectionConfFactory.createDataConnectionConfiguration());
        }

        serverFactory.addListener("default", listenerFactory.createListener());

        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        //userManagerFactory.setFile(new File(usersFilePath));
        userManagerFactory.setAdminName(username);
        userManager = userManagerFactory.createUserManager();

        try {
            initUser();
        } catch (FtpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverFactory.setUserManager(userManager);

        ftpServer = serverFactory.createServer();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            throw new RuntimeException(e);
        }
        System.out.println("FTP SERVER STARTOVAN NA:  " + host + ":" + port);
    }

    @PreDestroy
    private void stop() {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

    private void initUser() throws FtpException, IOException {
        boolean exist = userManager.doesExist(username);
        // need to init user
        if (!exist) {
            List<Authority> authorities = new ArrayList<>();
            authorities.add(new WritePermission());
            authorities.add(new ConcurrentLoginPermission(0, 0));
            BaseUser user = new BaseUser();
            user.setName(username);
            user.setPassword(password);
            user.setHomeDirectory(homeDir.getFile().getAbsolutePath());
            user.setMaxIdleTime(MAX_IDLE_TIME);
            user.setAuthorities(authorities);
            userManager.save(user);
        }
    }


    public void setPassword(UserInfo userInfo) throws FtpException {
        String username = userManager.getAdminName();
        User savedUser = userManager.authenticate(new UsernamePasswordAuthentication(username, userInfo.getOldPassword()));
        BaseUser baseUser = new BaseUser(savedUser);
        baseUser.setPassword(userInfo.getPassword());
        userManager.save(baseUser);
    }


    /*public void setHomeDir(String homeDir) throws FtpException, IOException {
        User userInfo = userManager.getUserByName(userManager.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        mkHomeDir(homeDir);
        baseUser.setHomeDirectory(homeDir);
        userManager.save(baseUser);

        Properties ftpProperties = PropertiesHelper.getProperties(CONFIG_FILE_NAME);
        if (!homeDir.endsWith("/")) {
            homeDir += "/";
        }
        ftpProperties.setProperty("ftp.home-dir", homeDir);
        PropertiesHelper.saveProperties(ftpProperties, CONFIG_FILE_NAME);
    }*/


    public void setMaxDownloadRate(int maxDownloadRate) throws FtpException {
        int maxUploadRate = getFTPInfo().getMaxUploadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }


    public void setMaxUploadRate(int maxUploadRate) throws FtpException {
        int maxDownloadRate = getFTPInfo().getMaxDownloadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }


    private void saveTransferRateInfo(int maxUploadRate, int maxDownloadRate) throws FtpException {
        User userInfo = userManager.getUserByName(userManager.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        authorities.add(new TransferRatePermission(maxDownloadRate, maxUploadRate));
        baseUser.setAuthorities(authorities);
        userManager.save(baseUser);
    }


    public FTPInfo getFTPInfo() throws FtpException {
        User userInfo = userManager.getUserByName(userManager.getAdminName());
        TransferRateRequest transferRateRequest = (TransferRateRequest) userInfo
                .authorize(new TransferRateRequest());
        File homeDir = Paths.get(userInfo.getHomeDirectory()).toFile();
        long totalSpace = homeDir.getTotalSpace();
        long usedSpace = totalSpace - homeDir.getUsableSpace();

        return new FTPInfo(host, port, homeDir.getAbsolutePath(),
                transferRateRequest.getMaxDownloadRate() / 1024,
                transferRateRequest.getMaxUploadRate() / 1024,
                usedSpace, totalSpace);
    }

    private void mkHomeDir(File homeDir) throws Exception {
       if(!homeDir.exists()) {
           boolean success = homeDir.mkdir();
           if(!success) throw new Exception("Error during home directory!");
       }
    }

    /*private void createConfigFile() throws IOException {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            boolean result = configFile.createNewFile();
            if (!result) {
                System.out.println("Greska prilikom kreiranja " + configFilePath + "fajla!");
            }
        }

        File usersFile = new File(usersFilePath);
        if (!usersFile.exists()) {
            boolean result = usersFile.createNewFile();
            if (!result) {
                System.out.println("Greska prilikom kreiranja " + usersFilePath + "fajla!");
            }
        }
    }*/
}