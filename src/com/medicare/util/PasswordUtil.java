package com.medicare.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/** Salted SHA-256 password hashing using only the JDK. Stored as "salt$hash". */
public class PasswordUtil {
    private PasswordUtil() { }

    public static String createHash(String password) {
        byte[] saltBytes = new byte[8];
        new SecureRandom().nextBytes(saltBytes);
        String salt = toHex(saltBytes);
        return salt + "$" + hash(salt, password);
    }

    public static boolean verify(String password, String stored) {
        String[] parts = stored.split("\\$");
        if (parts.length != 2) return false;
        byte[] a = hash(parts[0], password).getBytes(StandardCharsets.UTF_8);
        byte[] b = parts[1].getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(a, b);
    }

    private static String hash(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return toHex(md.digest((salt + password).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
