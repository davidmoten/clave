package com.github.davidmoten.clave;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Tokens {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String TOKEN_DELIMITER = "\t";

    static String computeToken(String username, CipherKey cipherKey) {
        try {
            Key aesKey = new SecretKeySpec(cipherKey.value, "AES");
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String info = username + TOKEN_DELIMITER + cipherKey.expiryTime;
            byte[] encrypted = cipher.doFinal(info.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    static Info parseToken(String token, CipherKey cipherKey) {
        byte[] decoded;
        try {
            byte[] tokenBytes = Base64.getDecoder().decode(token);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            Key aesKey = new SecretKeySpec(cipherKey.value, "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decoded = cipher.doFinal(tokenBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
                | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        String s = new String(decoded, StandardCharsets.UTF_8);
        String[] items = s.split(TOKEN_DELIMITER);
        return new Info(items[0], Long.parseLong(items[1]));
    }

    static final class Info {
        final String username;
        final long expiryTime;

        public Info(String username, long expiryTime) {
            this.username = username;
            this.expiryTime = expiryTime;
        }

    }
}
