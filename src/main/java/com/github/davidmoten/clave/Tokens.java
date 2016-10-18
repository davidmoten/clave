package com.github.davidmoten.clave;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

final class Tokens {

    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String TOKEN_DELIMITER = "\t";
    // TODO justify selection of salt length
    private static final int SALT_LENGTH = 16;

    static String createToken(String username, String password, byte[] cipherKey, Clock clock) {
        try {
            Key aesKey = new SecretKeySpec(cipherKey, SECRET_KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            String info = username + TOKEN_DELIMITER + password + TOKEN_DELIMITER + clock.now()
                    + UUID.randomUUID().toString().substring(0, SALT_LENGTH);
            byte[] encrypted = cipher.doFinal(info.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    static Info parseToken(String token, byte[] cipherKey) {
        byte[] decoded;
        try {
            byte[] tokenBytes = Base64.getDecoder().decode(token);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            Key aesKey = new SecretKeySpec(cipherKey, SECRET_KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decoded = cipher.doFinal(tokenBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException
                | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        String s = new String(decoded, StandardCharsets.UTF_8);
        String[] items = s.substring(0, s.length() - SALT_LENGTH).split(TOKEN_DELIMITER);
        return new Info(items[0], items[1], Long.parseLong(items[2]));
    }

    static final class Info {
        final String username;
        final String password;
        final long createTime;

        public Info(String username, String password, long createTime) {
            this.username = username;
            this.password = password;
            this.createTime = createTime;
        }

    }

    public static String createToken(String username, String password, byte[] cipherKey) {
        return createToken(username, password, cipherKey, Clock.DEFAULT_CLOCK);
    }
}
