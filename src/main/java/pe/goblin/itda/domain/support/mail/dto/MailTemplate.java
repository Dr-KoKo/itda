package pe.goblin.itda.domain.support.mail.dto;

public enum MailTemplate {
    EMAIL_VERIFICATION("/templates/mail-template.html");

    private final String path;

    MailTemplate(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
