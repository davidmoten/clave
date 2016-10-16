package com.github.davidmoten.clave;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

class Pbkdf2 {
	private static final int ITERATIONS = 1000;
	private static final int KEY_LENGTH = 192; // bits

	public static byte[] hashPassword(String password, byte[] salt) {
		char[] passwordChars = password.toCharArray();
		PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, ITERATIONS, KEY_LENGTH);
		try {
			SecretKeyFactory key = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return key.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

}