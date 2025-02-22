package pe.goblin.resourceservice.domain.support.mail.exception;

public abstract class MailException extends RuntimeException {
    protected MailException(String message) {
        super(message);
    }

    protected MailException(String message, Throwable cause) {
        super(message, cause);
    }
}
