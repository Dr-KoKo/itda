package pe.goblin.itda.domain.support.mail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pe.goblin.itda.domain.support.mail.dto.MailTemplate;
import pe.goblin.itda.domain.support.mail.exception.MailTemplateException;

import java.util.Map;

@SpringBootTest(classes = {MailTemplateResolver.class})
class MailTemplateResolverTest {
    @Autowired
    private MailTemplateResolver resolver;

    @Test
    void resolve() {
        MailTemplate mailTemplate = MailTemplate.EMAIL_VERIFICATION;
        String resultHtml = resolver.resolve(mailTemplate, Map.of("verificationCode", "123123"));
        Assertions.assertTrue(resultHtml.contains("123123"));
    }

    @Test
    void resolveNoParameters() {
        MailTemplate mailTemplate = MailTemplate.EMAIL_VERIFICATION;
        Assertions.assertThrows(MailTemplateException.class, () -> resolver.resolve(mailTemplate, null));
    }
}
