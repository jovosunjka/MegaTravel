package com.bsep_sbz.SIEMCenter.helper;

import org.apache.maven.shared.invoker.*;
import java.io.File;
import java.util.Arrays;

public class HelperMethods
{
    public static void mavenCleanAndInstallRules() throws MavenInvocationException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( "../SiemCenterRules/pom.xml" ) );
        request.setGoals( Arrays.asList( "clean", "install" ) );

        Invoker invoker = new DefaultInvoker();
        invoker.execute( request );
    }
}
