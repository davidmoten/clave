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

public final class Tokens {

	private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final String TOKEN_DELIMITER = "\t";
	//TODO justify selection of salt length
	private static final int SALT_LENGTH = 16;

	static String createToken(String username, byte[] cipherKey, Clock clock) {
		try {
			Key aesKey = new SecretKeySpec(cipherKey, "AES");
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			String info = username + TOKEN_DELIMITER + clock.now()
					+ UUID.randomUUID().toString().substring(0, SALT_LENGTH);
			byte[] encrypted = cipher.doFinal(info.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	static Info parseToken(String token, byte[] cipherKey) {
		byte[] decoded;
		try {
			byte[] tokenBytes = Base64.getDecoder().decode(token);
			Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			Key aesKey = new SecretKeySpec(cipherKey, "AES");
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			decoded = cipher.doFinal(tokenBytes);
		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
		String s = new String(decoded, StandardCharsets.UTF_8);
		String[] items = s.substring(0, s.length() - SALT_LENGTH).split(TOKEN_DELIMITER);
		return new Info(items[0], Long.parseLong(items[1]));
	}

	static final class Info {
		final String username;
		final long createTime;

		public Info(String username, long createTime) {
			this.username = username;
			this.createTime = createTime;
		}

	}

	private static final Clock DEFAULT_CLOCK = new Clock() {

		@Override
		public long now() {
			return System.currentTimeMillis();
		}
	};

	public static String createToken(String username, byte[] cipherKey) {
		return createToken(username, cipherKey, DEFAULT_CLOCK);
	}
}
