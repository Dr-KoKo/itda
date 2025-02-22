package pe.goblin.resourceserver.context;

import pe.goblin.resourceserver.domain.support.mail.dto.MailEvent;
import pe.goblin.resourceserver.domain.support.mail.dto.MailTemplate;
import pe.goblin.resourceserver.domain.support.mail.dto.Status;

import java.util.Map;

public class TestMailEvent extends MailEvent {
    public TestMailEvent(Long id, String receiver, String subject, MailTemplate template, Map<String, String> arguments, Status status) {
        super(id, receiver, subject, template, arguments, status);
    }
}
