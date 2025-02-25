package pe.goblin.resourceservice.domain.support.mail.exception;

public class MailTemplateException extends MailException {

    public MailTemplateException(String message) {
        super(message);
    }

    public MailTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
