package com.bsep_sbz.KeycloakUserStorageProvider.provider;

import javax.naming.InitialContext;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class MyKeycloakUserStorageProviderFactory implements UserStorageProviderFactory<MyKeycloakUserStorageProvider> {

    private static final Logger logger = Logger.getLogger(MyKeycloakUserStorageProviderFactory.class);
    private static final String MY_KEYCLOAK_USER_STORAGE_PROVIDER_NAME = "myKeycloakUserStorageProvider";

    @Override
    public MyKeycloakUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        logger.info("Created new MyKeycloakUserStorageProvider");
        try {
            InitialContext ctx = new InitialContext();
            MyKeycloakUserStorageProvider provider = (MyKeycloakUserStorageProvider)ctx.lookup("java:global/myKeyCloakDatabaseConnector/" + MyKeycloakUserStorageProvider.class.getSimpleName());
            provider.setModel(model);
            provider.setSession(session);
            provider.loadRoles();
            return provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return MY_KEYCLOAK_USER_STORAGE_PROVIDER_NAME;
    }

    @Override
    public String getHelpText() {
        return "myKeycloakUserStorageProvider to connect with database";
    }

    @Override
    public void close() {
        logger.info("Closing myKeycloakUserStorageProvider Factory.");
    }
}
