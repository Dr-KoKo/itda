package pe.goblin.resourceservice.domain.support.auth.exception;

public class AuthorizationClientException extends RuntimeException {
    public AuthorizationClientException(Throwable cause) {
        super(cause);
    }

    protected AuthorizationClientException(String message) {
        super(message);
    }

    protected AuthorizationClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
