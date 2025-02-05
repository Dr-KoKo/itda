package pe.goblin.itda.context;

import pe.goblin.itda.domain.support.mail.dto.MailEvent;
import pe.goblin.itda.domain.support.mail.dto.MailTemplate;
import pe.goblin.itda.domain.support.mail.dto.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public interface MailEventFixture {
    default MailEvent createTestMailEvent() {
        return createTestMailEvent("test", Status.REQUESTED);
    }

    default MailEvent createTestMailEvent(String subject) {
        if (subject == null) {
            throw new IllegalArgumentException("Subject must not be null");
        }
        return createTestMailEvent(subject, Status.REQUESTED);
    }

    default MailEvent createTestMailEvent(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }
        return createTestMailEvent("test", status);
    }

    default MailEvent createTestMailEvent(String subject, Status status) {
        if (subject == null || status == null) {
            throw new IllegalArgumentException("Subject and Status must not be null");
        }
        return createTestMailEvent(null, subject, status);
    }

    default MailEvent createTestMailEvent(Long id, String subject, Status status) {
        return new TestMailEvent(id, "donghar@naver.com", subject, MailTemplate.EMAIL_VERIFICATION, Map.of("verificationCode", "123123"), status);
    }

    default List<MailEvent> createTestMailEvents(int size) {
        return IntStream.range(1, size + 1)
                .mapToObj(i -> createTestMailEvent("test" + i))
                .toList();
    }

    default List<MailEvent> createTestMailEvents(int size, Status status) {
        return IntStream.range(1, size + 1)
                .mapToObj(i -> createTestMailEvent("test" + i, status))
                .toList();
    }

    default void assertContains(MailEvent actualMailEvent, MailEvent expectedMailEvent) {
        assertContains(List.of(actualMailEvent), List.of(expectedMailEvent));
    }

    default void assertContains(List<MailEvent> actualMailEvents, List<MailEvent> expectedMailEvent) {
        assertThat(actualMailEvents).hasSize(expectedMailEvent.size())
                .contains(expectedMailEvent.toArray(MailEvent[]::new));
    }

    default void assertContainsExactly(List<MailEvent> actualMailEvents, List<MailEvent> expectedMailEvent) {
        assertThat(actualMailEvents).hasSize(expectedMailEvent.size())
                .containsExactlyElementsOf(expectedMailEvent);
    }

    default void assertStatusIs(MailEvent mailEvent, Status status) {
        assertStatusIs(List.of(mailEvent), status);
    }

    default void assertStatusIs(List<MailEvent> mailEvents, Status status) {
        if (mailEvents.isEmpty()) {
            return;
        }
        assertThat(mailEvents).allMatch(mailEvent -> {
            switch (status) {
                case REQUESTED -> {
                    return mailEvent.isRequested();
                }
                case SENDING -> {
                    return mailEvent.isSending();
                }
                case SUCCESS -> {
                    return mailEvent.isSuccess();
                }
                case FAILED -> {
                    return mailEvent.isFailed();
                }
                default -> throw new IllegalStateException("Unexpected value: " + status);
            }
        });
    }

    default void assertUpdatedAtIsBeforeOrEqualTo(MailEvent mailEvent, ZonedDateTime expectedUpdatedAt) {
        assertThat(mailEvent.getUpdatedAt()).isBeforeOrEqualTo(expectedUpdatedAt);
    }
}
