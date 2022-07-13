package de.lundy.pinnerino.exception;

public class PinnerinoDatabaseException extends RuntimeException {

    public PinnerinoDatabaseException(String message) {
        super(message);
    }

    public PinnerinoDatabaseException(String message, Object... objects) {
        super(String.format(message, objects));
    }

}
