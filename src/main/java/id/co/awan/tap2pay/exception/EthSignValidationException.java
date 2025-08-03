package id.co.awan.tap2pay.exception;

public class EthSignValidationException extends Exception {

    public EthSignValidationException(String message) {
        super(message);
    }

    public EthSignValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
