package ru.kpfu.itis.Exceptions;

public class AudioSaveException extends Exception {

    public AudioSaveException() {
    }

    public AudioSaveException(String message) {
        super(message);
    }

    public AudioSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public AudioSaveException(Throwable cause) {
        super(cause);
    }
}
