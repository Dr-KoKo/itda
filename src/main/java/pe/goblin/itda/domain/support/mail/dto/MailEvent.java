package pe.goblin.itda.domain.support.mail.dto;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

public class MailEvent {
    private Long id;
    private String receiver;
    private String subject;
    private MailTemplate template;
    private Map<String, String> arguments;
    private Status status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public MailEvent() {
    }

    protected MailEvent(String receiver, String subject, MailTemplate template, Map<String, String> arguments) {
        this.receiver = receiver;
        this.subject = subject;
        this.template = template;
        this.arguments = arguments;
        this.status = Status.REQUESTED;
    }

    protected MailEvent(Long id, String receiver, String subject, MailTemplate template, Map<String, String> arguments, Status status) {
        this.id = id;
        this.receiver = receiver;
        this.subject = subject;
        this.template = template;
        this.arguments = arguments;
        this.status = status;
    }

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

    public boolean isRequested() {
        return status == Status.REQUESTED;
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

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MailEvent mailEvent)) return false;
        return Objects.equals(id, mailEvent.id) && Objects.equals(receiver, mailEvent.receiver) && Objects.equals(subject, mailEvent.subject) && template == mailEvent.template && Objects.equals(arguments, mailEvent.arguments) && status == mailEvent.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiver, subject, template, arguments, status);
    }

    @Override
    public String toString() {
        return "MailEvent{" +
                "arguments=" + arguments +
                ", id=" + id +
                ", receiver='" + receiver + '\'' +
                ", subject='" + subject + '\'' +
                ", template=" + template +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
