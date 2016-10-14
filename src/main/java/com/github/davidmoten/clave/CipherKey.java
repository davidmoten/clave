package com.github.davidmoten.clave;

final class CipherKey {
    final byte[] value;
    final long expiryTime;

    CipherKey(byte[] value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }

}