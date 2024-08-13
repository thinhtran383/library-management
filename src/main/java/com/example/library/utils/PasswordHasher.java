package com.example.library.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;

public class PasswordHasher {


    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        String password = "thinh123";
        String hashedPassword = hashPassword(password);
        System.out.println("Hashed Password: " + hashedPassword);

        Provider[] providers = Security.getProviders();
        for (Provider provider : providers) {
            System.out.println(provider.getName() + " " + provider.getVersion());
        }
    }
}
