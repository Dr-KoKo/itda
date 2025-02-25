package pe.goblin.resourceservice.domain.support.mail.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {MailSenderAutoConfiguration.class, MailClient.class})
@Tag("external")
class MailClientTest {
    private static final Logger log = LoggerFactory.getLogger(MailClientTest.class);

    @Autowired
    private MailClient mailClient;

    @Test
    void send() {
        log.info("send mail started");

        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test", "test", true));

        log.info("send mail ended");
    }

    @Test
    void sendInRow() {
        log.info("send mail started");

        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test1", "test1", true));
        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test2", "test2", true));
        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test3", "test3", true));
        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test4", "test4", true));
        mailClient.send(new MailClient.MailRequest("donghar@naver.com", "test5", "test5", true));

        log.info("send mail ended");
    }

    @Test
    void sendInBulk() {
        log.info("send mail started");

        MailClient.MailRequest[] mailRequests = new MailClient.MailRequest[]{
                new MailClient.MailRequest("donghar@naver.com", "test1", "test1", true),
                new MailClient.MailRequest("donghar@naver.com", "test2", "test2", true),
                new MailClient.MailRequest("donghar@naver.com", "test3", "test3", true),
                new MailClient.MailRequest("donghar@naver.com", "test4", "test4", true),
                new MailClient.MailRequest("donghar@naver.com", "test5", "test5", true)
        };
        mailClient.send(mailRequests);

        log.info("send mail ended");
    }
}
