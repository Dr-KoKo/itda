package pe.goblin.resourceserver.domain.support.mail.repository;

import pe.goblin.resourceserver.domain.support.mail.dto.MailEvent;
import pe.goblin.resourceserver.domain.support.mail.dto.Status;

import java.util.List;
import java.util.Optional;

public interface MailEventRepository {
    MailEvent save(MailEvent mailEvent);

    Optional<MailEvent> findById(Long id);

    List<MailEvent> findByStatusOrderByCreatedAt(Status status, int limit);

    void updateStatus(Long id, Status status);

    void updateStatus(List<Long> ids, Status status);
}
