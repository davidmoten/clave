package com.github.davidmoten.clave;

public final class HashAndSalt {
	final byte[] salt;
	final byte[] hash;

	public HashAndSalt(byte[] hash, byte[] salt) {
		this.hash = hash;
		this.salt = salt;
	}

}
