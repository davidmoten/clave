package com.github.davidmoten.clave;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public final class CipherKey {

    private static final String AES = "AES";
    private static final int AES_KEY_BITS = 128;// multiple of 8
	private final byte[] aesKey;

    CipherKey() {
    	this.aesKey = createAesKey();
    }
    
    public byte[] value() {
    	return aesKey;
    }

    private static byte[] createAesKey() {
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
