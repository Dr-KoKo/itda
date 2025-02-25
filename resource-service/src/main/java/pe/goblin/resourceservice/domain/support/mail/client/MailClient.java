package pe.goblin.resourceservice.domain.support.mail.client;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import pe.goblin.resourceservice.domain.support.mail.exception.MailTransportException;

import java.util.Arrays;

@Component
public class MailClient {
    private final JavaMailSender mailSender;

    public MailClient(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(MailRequest request) throws MailTransportException {
        try {
            if (request.isHtml()) {
                MimeMessage mimeMessage = createMimeMessage(request.getTo(), request.getSubject(), request.getText());
                mailSender.send(mimeMessage);
            } else {
                SimpleMailMessage simpleMessage = createSimpleMessage(request.getTo(), request.getSubject(), request.getText());
                mailSender.send(simpleMessage);
            }
        } catch (org.springframework.mail.MailException e) {
            throw new MailTransportException("failed to send message", e);
        }
    }

    public void send(MailRequest... requests) throws MailTransportException {
        MimeMessage[] mimeMessages = Arrays.stream(requests)
                .map(request -> createMimeMessage(request.getTo(), request.getSubject(), request.getText()))
                .toArray(MimeMessage[]::new);
        try {
            mailSender.send(mimeMessages);
        } catch (org.springframework.mail.MailException e) {
            throw new MailTransportException("failed to send message", e);
        }
    }

    private SimpleMailMessage createSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        return message;
    }

    private MimeMessage createMimeMessage(String to, String subject, String html) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, false, "utf-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
        } catch (MessagingException e) {
            throw new MailTransportException("failed to create MimeMessage", e);
        }
        return message;
    }

    public static class MailRequest {
        private String to;
        private String subject;
        private String text;
        private boolean html;

        public MailRequest(String to, String subject, String text, boolean isHtml) {
            this.to = to;
            this.subject = subject;
            this.text = text;
            this.html = isHtml;
        }

        public String getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getText() {
            return text;
        }

        public boolean isHtml() {
            return html;
        }
    }
}
