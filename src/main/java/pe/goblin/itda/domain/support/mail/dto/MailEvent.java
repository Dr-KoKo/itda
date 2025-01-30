package pe.goblin.itda.domain.support.mail.dto;

import java.util.Map;

public class MailEvent {
    private Long id;
    private String receiver;
    private String subject;
    private MailTemplate template;
    private Map<String, String> arguments;
    private Status status;

    public Long getId() {
        return id;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSubject() {
        return subject;
    }

    public MailTemplate getTemplate() {
        return template;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public boolean isSending() {
        return status == Status.SENDING;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isFailed() {
        return status == Status.FAILED;
    }
}
