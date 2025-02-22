package pe.goblin.resourceserver.domain.support.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pe.goblin.resourceserver.domain.support.mail.MailTemplateResolver;
import pe.goblin.resourceserver.domain.support.mail.client.MailClient;
import pe.goblin.resourceserver.domain.support.mail.dto.MailEvent;
import pe.goblin.resourceserver.domain.support.mail.dto.Status;
import pe.goblin.resourceserver.domain.support.mail.exception.MailTemplateException;
import pe.goblin.resourceserver.domain.support.mail.exception.MailTransportException;
import pe.goblin.resourceserver.domain.support.mail.repository.MailEventRepository;

import java.util.*;

/**
 * Do not declare {@link Transactional}
 */
@Service
public class OutboxMailService {
    private static final Logger log = LoggerFactory.getLogger(OutboxMailService.class);
    private static final int BULK_SIZE = 10;

    private final MailClient mailClient;
    private final MailEventRepository mailEventRepository;
    private final MailTemplateResolver mailTemplateResolver;
    private final TransactionTemplate transactionTemplate;

    public OutboxMailService(MailClient mailClient, MailEventRepository mailEventRepository, MailTemplateResolver mailTemplateResolver, TransactionTemplate transactionTemplate) {
        this.mailClient = mailClient;
        this.mailEventRepository = mailEventRepository;
        this.mailTemplateResolver = mailTemplateResolver;
        this.transactionTemplate = transactionTemplate;
    }

    public MailResult send() {
        List<MailEvent> mailEvents = fetchMailEvents();
        MailResult result = new MailResult(mailEvents.size());
        List<MailEventHolder> mailRequests = prepareMailRequests(mailEvents, result);
        sendMails(mailRequests, result);
        return result;
    }

    private List<MailEvent> fetchMailEvents() {
        return mailEventRepository.findByStatusOrderByCreatedAt(Status.REQUESTED, BULK_SIZE);
    }

    private List<MailEventHolder> prepareMailRequests(List<MailEvent> mailEvents, MailResult result) {
        List<MailEventHolder> mailEventHolders = new ArrayList<>();
        for (MailEvent event : mailEvents) {
            updateStatus(event.getId(), Status.SENDING);
            String htmlContent;
            try {
                htmlContent = mailTemplateResolver.resolve(event.getTemplate(), event.getArguments());
            } catch (MailTemplateException e) {
                handleTemplateResolveFailure(event, e, result);
                continue;
            }
            mailEventHolders.add(new MailEventHolder(event, htmlContent));
        }
        return mailEventHolders;
    }

    private void handleTemplateResolveFailure(MailEvent mailEvent, MailTemplateException exception, MailResult result) {
        log.error("Template resolve failure for MailEvent ID: {}", mailEvent.getId(), exception);
        updateStatus(mailEvent.getId(), Status.FAILED);
        result.addException(mailEvent.getId(), exception);
    }

    private void sendMails(List<MailEventHolder> mailHolders, MailResult result) {
        if (mailHolders.isEmpty()) return;

        try {
            mailClient.send(mailHolders.stream().map(MailEventHolder::getMailRequest).toArray(MailClient.MailRequest[]::new));
            List<Long> mailEventIds = mailHolders.stream().map(MailEventHolder::getId).toList();
            updateStatus(mailEventIds, Status.SUCCESS);
        } catch (MailTransportException e) {
            handleMailClientFailure(mailHolders, e, result);
        }
    }

    private void handleMailClientFailure(List<MailEventHolder> mailEventHolders, MailTransportException exception, MailResult result) {
        List<Long> mailEventIds = mailEventHolders.stream().map(MailEventHolder::getId).toList();
        log.error("Mail client failure for MailEvent IDs: {}", mailEventIds, exception);
        updateStatus(mailEventIds, Status.FAILED);
        result.addException(mailEventIds, exception);
    }

    private void updateStatus(Long mailEventId, Status status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> mailEventRepository.updateStatus(mailEventId, status));
    }

    private void updateStatus(List<Long> mailEventIds, Status status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> mailEventRepository.updateStatus(mailEventIds, status));
    }

    private static class MailEventHolder {
        private final MailEvent mailEvent;
        private final String content;

        MailEventHolder(MailEvent mailEvent, String content) {
            this.mailEvent = mailEvent;
            this.content = content;
        }

        Long getId() {
            return mailEvent.getId();
        }

        MailClient.MailRequest getMailRequest() {
            return new MailClient.MailRequest(mailEvent.getReceiver(), mailEvent.getSubject(), content, true);
        }
    }

    public static class MailResult {
        private final int requested;
        private final Map<Long, Throwable> exceptions;

        MailResult(int requested) {
            this.requested = requested;
            this.exceptions = new HashMap<>();
        }

        public void addException(Long mailEventId, Throwable exception) {
            exceptions.put(mailEventId, exception);
        }

        public void addException(Iterable<Long> mailEventId, Throwable exception) {
            mailEventId.forEach(id -> exceptions.put(id, exception));
        }

        public int getRequested() {
            return requested;
        }

        public int getSuccess() {
            return requested - exceptions.size();
        }

        public int getFailed() {
            return exceptions.size();
        }

        public Map<Long, Throwable> getExceptions() {
            return Collections.unmodifiableMap(exceptions);
        }
    }
}
