package ru.kpfu.itis.Exceptions;

public class TextRecognitionException extends Exception {

    public TextRecognitionException() {
    }

    public TextRecognitionException(String message) {
        super(message);
    }

    public TextRecognitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextRecognitionException(Throwable cause) {
        super(cause);
    }
}
