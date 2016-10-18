package com.github.davidmoten.clave;

import java.security.SecureRandom;
import java.util.Arrays;
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
		if (values.containsKey(username)){
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
	
	private  byte[] createSalt () {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		random.nextBytes(salt);
		return salt;
	}

}
