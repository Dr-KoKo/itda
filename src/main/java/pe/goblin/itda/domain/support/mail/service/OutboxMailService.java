package pe.goblin.itda.domain.support.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import pe.goblin.itda.domain.support.mail.MailTemplateResolver;
import pe.goblin.itda.domain.support.mail.client.MailClient;
import pe.goblin.itda.domain.support.mail.dto.MailEvent;
import pe.goblin.itda.domain.support.mail.dto.Status;
import pe.goblin.itda.domain.support.mail.exception.MailTemplateException;
import pe.goblin.itda.domain.support.mail.exception.MailTransportException;
import pe.goblin.itda.domain.support.mail.repository.MailEventRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<MailClient.MailRequest, Long> mailRequestToEventId = new HashMap<>();
        List<MailClient.MailRequest> mailRequests = prepareMailRequests(mailEvents, mailRequestToEventId, result);
        sendMails(mailRequests, mailRequestToEventId, result);
        return result;
    }

    private List<MailEvent> fetchMailEvents() {
        return mailEventRepository.findByStatusOrderByCreatedAt(Status.REQUESTED, BULK_SIZE);
    }

    private List<MailClient.MailRequest> prepareMailRequests(List<MailEvent> mailEvents, Map<MailClient.MailRequest, Long> mailRequestToEventId, MailResult result) {
        List<MailClient.MailRequest> mailRequests = new ArrayList<>();
        for (MailEvent event : mailEvents) {
            updateStatus(event.getId(), Status.SENDING);
            String htmlContent;
            try {
                htmlContent = mailTemplateResolver.resolve(event.getTemplate(), event.getArguments());
            } catch (MailTemplateException e) {
                handleTemplateResolveFailure(event, e, result);
                continue;
            }
            MailClient.MailRequest mailRequest = new MailClient.MailRequest(event.getReceiver(), event.getSubject(), htmlContent, true);
            mailRequests.add(mailRequest);
            mailRequestToEventId.put(mailRequest, event.getId());
        }
        return mailRequests;
    }

    private void handleTemplateResolveFailure(MailEvent mailEvent, MailTemplateException exception, MailResult result) {
        log.error("Template resolve failure", exception);
        updateStatus(mailEvent.getId(), Status.FAILED);
        result.addException(mailEvent.getId(), exception);
    }

    private void sendMails(List<MailClient.MailRequest> mailRequests, Map<MailClient.MailRequest, Long> mailRequestToEventId, MailResult result) {
        if (mailRequests.isEmpty()) return;

        try {
            mailClient.send(mailRequests.toArray(MailClient.MailRequest[]::new));
            List<Long> mailEventIds = mailRequests.stream().map(mailRequestToEventId::get).toList();
            updateStatus(mailEventIds, Status.SUCCESS);
        } catch (MailTransportException e) {
            handleMailClientFailure(mailRequests, mailRequestToEventId, e, result);
        }
    }

    private void handleMailClientFailure(List<MailClient.MailRequest> mailRequests, Map<MailClient.MailRequest, Long> mailRequestToEventId, MailTransportException exception, MailResult result) {
        log.error("Mail client failure", exception);
        List<Long> mailEventIds = mailRequests.stream().map(mailRequestToEventId::get).toList();
        updateStatus(mailEventIds, Status.FAILED);
        result.addException(mailRequestToEventId.values(), exception);
    }

    private void updateStatus(Long mailEventId, Status status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> mailEventRepository.updateStatus(mailEventId, status));
    }

    private void updateStatus(List<Long> mailEventIds, Status status) {
        transactionTemplate.executeWithoutResult(transactionStatus -> mailEventRepository.updateStatus(mailEventIds, status));
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
            return exceptions;
        }
    }
}
