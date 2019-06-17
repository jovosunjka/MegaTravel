package com.bsep_sbz.SIEMCenter.helper;

import org.apache.maven.shared.invoker.*;
import java.io.File;
import java.util.Arrays;

public class HelperMethods
{
    public static void mavenCleanAndInstallRules() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( "..\\SiemCenterRules\\pom.xml" ) );
        request.setGoals( Arrays.asList( "clean", "install" ) );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );
    }

    public static String getUsername(String message) {
        if(message != null) {
            if (message.contains("username")) {
                String[] splittedMessage = message.split("\\s+");
                int i = 0;
                boolean found = false;
                for (; i < splittedMessage.length; i++) {
                    if (splittedMessage[i].equals("username")) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    String username = splittedMessage[++i];
                    return username.replaceAll("'", "");
                }
            }
        }

        return "";
    }
}
