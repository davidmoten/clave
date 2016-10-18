package com.github.davidmoten.clave;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.github.davidmoten.clave.Tokens.Info;
import com.github.davidmoten.rx.Bytes;

public final class Store {

    private static final String AES_TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static final String AES = "AES";

    private static final Store instance = new Store();

    private static final int SALT_LENGTH = 20;

    private final Map<String, HashAndSalt> values = new ConcurrentHashMap<String, HashAndSalt>();
    private final CipherKey cipherKey = new CipherKey();

    public static Store instance() {
        return instance;
    }

    public byte[] cipherKey() {
        return cipherKey.value();
    }

    public void createAccount(String username, String password) {
        if (values.containsKey(username)) {
            throw new UsernameExistsAlreadyException();
        }
        byte[] salt = createSalt();
        byte[] hash = Pbkdf2.hashPassword(password, salt);
        values.put(username, new HashAndSalt(hash, salt));
    }

    public void changePassword(String username, String currentPassword, String newPassword) {
        HashAndSalt hs = values.get(username);
        if (hs == null) {
            throw new WrongUsernameOrPasswordException();
        } else {
            byte[] hash1 = Pbkdf2.hashPassword(currentPassword, hs.salt);
            if (!Arrays.equals(hash1, hs.hash)) {
                throw new WrongUsernameOrPasswordException();
            } else {
                byte[] salt = createSalt();
                byte[] hash = Pbkdf2.hashPassword(newPassword, salt);
                values.put(username, new HashAndSalt(hash, salt));
            }
        }
    }

    private byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    public String getPassword(String token, String passwordId, long timeoutMs, Clock clock) {
        Info info = Tokens.parseToken(token, cipherKey.value());
        if (info.createTime < clock.now() - timeoutMs) {
            throw new SessionTimeoutException();
        } else {
            try {
                // TODO use InputStream instead for efficiency
                byte[] bytes = getEncryptedArchive(info.username);
                byte[] salt = getPasswordSalt(info.username);
                byte[] key = concatenate(info.password.getBytes(StandardCharsets.UTF_8), salt);
                SecretKeySpec sks = new SecretKeySpec(key, AES);
                Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, sks);
                byte[] bytes2 = cipher.doFinal(bytes);
                InputStream is = new ByteArrayInputStream(bytes2, 0, bytes2.length - SALT_LENGTH);
                // first entry is xml index with
                // id, name, username, url, passwordId, passwordSalt, notes,
                // tags, attachments (name, bytes)
                // following entries are salt then encrypted bytes pairs
                byte[] passwordBytes = Bytes.unzip(is) //
                        .first(entry -> entry.getName().equals(passwordId)) //
                        .flatMap(entry -> Bytes.from(entry.getInputStream())) //
                        .to(Bytes.collect()) //
                        .toBlocking().single();
                return "boo";
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                    | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private byte[] getPasswordSalt(String username) {
        return "salt".getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getEncryptedArchiveSalt(String username) {
        // TODO
        return "salt".getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getEncryptedArchive(String username) {
        // TODO
        return "boo".getBytes();
    }

}
