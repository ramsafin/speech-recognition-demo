package ru.kpfu.itis.Exceptions;

public class IllegalFilePathException extends Exception {

    public IllegalFilePathException() {
    }

    public IllegalFilePathException(String message) {
        super(message);
    }

    public IllegalFilePathException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalFilePathException(Throwable cause) {
        super(cause);
    }
}
