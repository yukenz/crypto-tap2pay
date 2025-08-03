package id.co.awan.tap2pay.exception;

public class EthSignTypeValidationException extends Exception {

    public EthSignTypeValidationException(String message) {
        super(message);
    }

    public EthSignTypeValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
