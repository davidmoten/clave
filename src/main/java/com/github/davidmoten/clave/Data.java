package com.github.davidmoten.clave;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Data {

    private static final Data instance = new Data();

    private static final long ROOT_EXPIRY_MS = TimeUnit.DAYS.toMillis(1);
    private static final String AES = "AES";
    private static final int AES_KEY_BITS = 128;// multiple of 8

    public static Data instance() {
        return instance;
    }

    private Data() {

    }

    private final Map<String, CipherKey> userCipherKeys = new ConcurrentHashMap<String, CipherKey>();

    public CipherKey getOrComputeCipherKey(String username) {
        return userCipherKeys.compute(username, (name, c) -> computeRoot(name, c));
    }
    
    public Optional<CipherKey> getCipherKey(String username) {
        return Optional.of(userCipherKeys.get(username));
    }

    private static CipherKey computeRoot(String name, CipherKey c) {
        if (c == null || c.expiryTime < System.currentTimeMillis()) {
            return new CipherKey(nextValue(), System.currentTimeMillis() + ROOT_EXPIRY_MS);
        } else {
            return c;
        }
    }

    private static byte[] nextValue() {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(AES);
            kgen.init(AES_KEY_BITS);
            SecretKey key = kgen.generateKey();
            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
