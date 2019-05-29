package com.bsep_sbz.SIEMCenter.ftp.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wyp0596 on 8/12/2016.
 */
public class PropertiesHelper {


    public static Properties getProperties(String inputFilePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(inputFilePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    public static void saveProperties(Properties properties, String outputFilePath) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath)) {
            properties.store(fileOutputStream);
        }
    }
}
