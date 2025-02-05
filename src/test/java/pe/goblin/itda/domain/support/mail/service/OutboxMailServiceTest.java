package pe.goblin.itda.domain.support.mail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;
import pe.goblin.itda.context.MailEventFixture;
import pe.goblin.itda.domain.support.mail.MailTemplateResolver;
import pe.goblin.itda.domain.support.mail.client.MailClient;
import pe.goblin.itda.domain.support.mail.dto.MailEvent;
import pe.goblin.itda.domain.support.mail.dto.MailTemplate;
import pe.goblin.itda.domain.support.mail.dto.Status;
import pe.goblin.itda.domain.support.mail.exception.MailException;
import pe.goblin.itda.domain.support.mail.exception.MailTemplateException;
import pe.goblin.itda.domain.support.mail.exception.MailTransportException;
import pe.goblin.itda.domain.support.mail.repository.MailEventRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxMailServiceTest implements MailEventFixture {
    private OutboxMailService outboxMailService;
    private MailClient mockMailClient;
    private MailEventRepository mockMailEventRepository;
    private MailTemplateResolver mockMailTemplateResolver;
    private TransactionTemplate mockTransactionTemplate;

    @BeforeEach
    void setUp() {
        mockMailClient = Mockito.mock(MailClient.class);
        mockMailEventRepository = Mockito.mock(MailEventRepository.class);
        mockMailTemplateResolver = Mockito.mock(MailTemplateResolver.class);
        mockTransactionTemplate = Mockito.mock(TransactionTemplate.class);
        outboxMailService = new OutboxMailService(mockMailClient, mockMailEventRepository, mockMailTemplateResolver, mockTransactionTemplate);
    }

    @Nested
    class Send {
        @Test
        void must_invoke_mail_client_when_mail_event_is_requested() {
            // given
            MailEvent mailEvent = createTestMailEvent(1L, "test", Status.REQUESTED);
            String resolvedHtml = "<h1>some-html</h1>";

            // when
            when(mockMailEventRepository.findByStatusOrderByCreatedAt(any(Status.class), anyInt())).thenReturn(List.of(mailEvent));
            when(mockMailTemplateResolver.resolve(any(MailTemplate.class), anyMap())).thenReturn(resolvedHtml);
            doNothing().when(mockTransactionTemplate).executeWithoutResult(Mockito.any());
            doNothing().when(mockMailClient).send(any(MailClient.MailRequest[].class));

            // then
            OutboxMailService.MailResult result = outboxMailService.send();
            verify(mockMailClient, times(1)).send(any(MailClient.MailRequest[].class));
            assertThat(result).hasFieldOrPropertyWithValue("requested", 1)
                    .hasFieldOrPropertyWithValue("success", 1);
        }

        @Test
        void must_add_exception_when_template_resolution_fails() {
            // given
            MailEvent mailEvent = createTestMailEvent(1L, "test", Status.REQUESTED);

            // when
            when(mockMailEventRepository.findByStatusOrderByCreatedAt(any(Status.class), anyInt())).thenReturn(List.of(mailEvent));
            when(mockMailTemplateResolver.resolve(any(MailTemplate.class), anyMap())).thenThrow(MailTemplateException.class);
            doNothing().when(mockTransactionTemplate).executeWithoutResult(Mockito.any());

            // then
            OutboxMailService.MailResult result = outboxMailService.send();
            verify(mockMailClient, times(0)).send(any(MailClient.MailRequest[].class));
            assertThat(result).hasFieldOrPropertyWithValue("requested", 1)
                    .hasFieldOrProperty("exceptions");
            assertThat(result.getExceptions()).hasSize(1)
                    .containsKey(1L)
                    .values().allMatch(MailException.class::isInstance);
        }

        @Test
        void must_add_exception_when_mail_client_fails() {
            // given
            MailEvent mailEvent = createTestMailEvent(1L, "test", Status.REQUESTED);
            String resolvedHtml = "<h1>some-html</h1>";

            // when
            when(mockMailEventRepository.findByStatusOrderByCreatedAt(any(Status.class), anyInt())).thenReturn(List.of(mailEvent));
            when(mockMailTemplateResolver.resolve(any(MailTemplate.class), anyMap())).thenReturn(resolvedHtml);
            doNothing().when(mockTransactionTemplate).executeWithoutResult(Mockito.any());
            doThrow(MailTransportException.class).when(mockMailClient).send(any(MailClient.MailRequest[].class));

            // then
            OutboxMailService.MailResult result = outboxMailService.send();
            verify(mockMailClient, times(1)).send(any(MailClient.MailRequest[].class));
            assertThat(result).hasFieldOrPropertyWithValue("requested", 1)
                    .hasFieldOrProperty("exceptions");
            assertThat(result.getExceptions()).hasSize(1)
                    .containsKey(1L)
                    .values().allMatch(MailException.class::isInstance);
        }
    }
}
