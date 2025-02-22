package pe.goblin.resourceserver.domain.support.mail.exception;

public class MailTransportException extends MailException {
    private static final long serialVersionUID = 1L;

    public MailTransportException(String message) {
        super(message);
    }

    public MailTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
