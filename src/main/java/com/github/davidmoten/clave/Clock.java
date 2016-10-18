package com.github.davidmoten.clave;

public interface Clock {

    long now();

    public static final Clock DEFAULT_CLOCK = new Clock() {

        @Override
        public long now() {
            return System.currentTimeMillis();
        }
    };

}
