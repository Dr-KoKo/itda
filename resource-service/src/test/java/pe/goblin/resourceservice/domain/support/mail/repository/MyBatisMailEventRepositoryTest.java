package pe.goblin.resourceservice.domain.support.mail.repository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pe.goblin.resourceservice.context.MailEventFixture;
import pe.goblin.resourceservice.context.MyBatisRepositoryTest;
import pe.goblin.resourceservice.domain.support.mail.dto.MailEvent;
import pe.goblin.resourceservice.domain.support.mail.dto.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MyBatisMailEventRepositoryTest extends MyBatisRepositoryTest implements MailEventFixture {
    @Autowired
    private MyBatisMailEventRepository myBatisMailEventRepository;

    @Nested
    class Save {
        @Test
        void must_insert_record() {
            // given
            MailEvent mailEvent = createTestMailEvent();

            // then
            myBatisMailEventRepository.save(mailEvent);

            Optional<MailEvent> findMailEventOpt = myBatisMailEventRepository.findById(mailEvent.getId());
            assertThat(findMailEventOpt).isPresent();
            assertContains(findMailEventOpt.get(), mailEvent);
        }
    }

    @Nested
    class SaveAll {
        @Test
        void must_insert_all_record() {
            // given
            int size = 3;
            List<MailEvent> mailEvents = createTestMailEvents(size);

            // then
            myBatisMailEventRepository.saveAll(mailEvents);

            List<Long> savedMailEventIds = extractMailEventIds(mailEvents);
            List<MailEvent> findMailEvents = myBatisMailEventRepository.findAllById(savedMailEventIds);
            assertContains(findMailEvents, mailEvents);
        }
    }

    private List<Long> extractMailEventIds(List<MailEvent> mailEvents) {
        return mailEvents.stream().map(MailEvent::getId).toList();
    }

    @Nested
    class FindByStatus {
        @Test
        void must_select_record_with_the_status() {
            // given
            int size = 5;
            Status status = Status.FAILED;
            List<MailEvent> mailEvents = createTestMailEvents(size, status);
            int limit = 50;

            // when
            myBatisMailEventRepository.saveAll(mailEvents);

            // then
            List<MailEvent> findMailEvents = myBatisMailEventRepository.findAllByStatus(status, limit);
            assertStatusIs(findMailEvents, status);
        }

        @Test
        void must_select_record_with_the_limit() {
            // given
            int size = 5;
            Status status = Status.FAILED;
            List<MailEvent> mailEvents = createTestMailEvents(size, status);
            int limit = 3;

            // then
            myBatisMailEventRepository.saveAll(mailEvents);

            // then
            List<MailEvent> findMailEvents = myBatisMailEventRepository.findAllByStatus(status, limit);
            assertThat(findMailEvents).hasSizeLessThanOrEqualTo(limit);
            assertStatusIs(mailEvents, status);
        }
    }

    @Nested
    class UpdateStatus {
        @Test
        void must_change_status() {
            // given
            Status statusBefore = Status.SENDING;
            MailEvent mailEvent = createTestMailEvent(statusBefore);
            Status statusAfter = Status.FAILED;

            // when
            myBatisMailEventRepository.save(mailEvent);
            Long savedMailEventId = mailEvent.getId();

            // then
            myBatisMailEventRepository.updateStatus(savedMailEventId, statusAfter);

            Optional<MailEvent> findMailEventOpt = myBatisMailEventRepository.findById(savedMailEventId);
            assertThat(findMailEventOpt).isPresent();
            assertThat(findMailEventOpt.get())
                    .satisfies(findMailEvent -> {
                        assertStatusIs(findMailEvent, statusAfter);
                        assertUpdatedAtIsBeforeOrEqualTo(findMailEvent, ZonedDateTime.now());
                    });
        }
    }

    @Nested
    class UpdateStatusInBulk {
        @Test
        void must_change_status() {
            // given
            int size = 5;
            Status statusBefore = Status.SENDING;
            List<MailEvent> mailEvents = createTestMailEvents(size, statusBefore);
            int limit = 50;
            Status statusAfter = Status.SUCCESS;

            // when
            myBatisMailEventRepository.saveAll(mailEvents);
            List<Long> savedMailEventIds = extractMailEventIds(mailEvents);

            // then
            myBatisMailEventRepository.updateStatusInBulk(savedMailEventIds, statusAfter);
            List<MailEvent> findMailEvents = myBatisMailEventRepository.findAllByStatus(statusAfter, limit);
            assertThat(findMailEvents).hasSizeLessThanOrEqualTo(limit);
            assertStatusIs(findMailEvents, statusAfter);
        }
    }
}
