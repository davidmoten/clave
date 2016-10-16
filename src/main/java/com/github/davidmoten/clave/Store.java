package com.github.davidmoten.clave;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Store {

	private static final Store instance = new Store();

	private static final int SALT_LENGTH = 20;

	private final Map<String, HashAndSalt> values = new ConcurrentHashMap<String, HashAndSalt>();

	public static Store instance() {
		return instance;
	}

	public void createAccount(String username, String password) {
		byte[] salt = createSalt();
		byte[] hash = Pbkdf2.hashPassword(password, salt);
		values.put(username, new HashAndSalt(hash, salt));
	}

	private  byte[] createSalt () {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		random.nextBytes(salt);
		return salt;
	}

}
