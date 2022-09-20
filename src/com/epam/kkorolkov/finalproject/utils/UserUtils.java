package com.epam.kkorolkov.finalproject.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Properties;

public class UserUtils {
    private static final String UTILS_PROPERTIES_FILE = "utils.properties";
    private static String salt;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream utilsStream = classLoader.getResourceAsStream(UTILS_PROPERTIES_FILE)) {
            Properties utilsProperties = new Properties();
            utilsProperties.load(utilsStream);
            salt = utilsProperties.getProperty("salt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

public static String hash(String password) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] bytes = md.digest((salt + password).getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new IllegalArgumentException(e);
    }
}





    public static boolean validate(String name, String email, String password) {
        //TODO user input validation
        return true;
    }
}
