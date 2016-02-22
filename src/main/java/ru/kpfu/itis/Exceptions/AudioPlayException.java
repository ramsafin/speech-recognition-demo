package ru.kpfu.itis.Exceptions;

public class AudioPlayException extends Exception {
    public AudioPlayException(String message) {
        super(message);
    }

    public AudioPlayException(String message, Throwable cause) {
        super(message, cause);
    }

    public AudioPlayException(Throwable cause) {
        super(cause);
    }
}
