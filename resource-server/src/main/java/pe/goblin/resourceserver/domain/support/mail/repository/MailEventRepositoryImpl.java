package pe.goblin.resourceserver.domain.support.mail.repository;

import org.springframework.stereotype.Repository;
import pe.goblin.resourceserver.domain.support.mail.dto.MailEvent;
import pe.goblin.resourceserver.domain.support.mail.dto.Status;

import java.util.List;
import java.util.Optional;

@Repository
public class MailEventRepositoryImpl implements MailEventRepository {
    private final MyBatisMailEventRepository myBatisMailEventRepository;

    public MailEventRepositoryImpl(MyBatisMailEventRepository myBatisMailEventRepository) {
        this.myBatisMailEventRepository = myBatisMailEventRepository;
    }

    @Override
    public MailEvent save(MailEvent mailEvent) {
        myBatisMailEventRepository.save(mailEvent);
        return mailEvent;
    }

    @Override
    public Optional<MailEvent> findById(Long id) {
        return myBatisMailEventRepository.findById(id);
    }

    @Override
    public List<MailEvent> findByStatusOrderByCreatedAt(Status status, int limit) {
        return myBatisMailEventRepository.findAllByStatus(status, limit);
    }

    @Override
    public void updateStatus(Long id, Status status) {
        myBatisMailEventRepository.updateStatus(id, status);
    }

    @Override
    public void updateStatus(List<Long> ids, Status status) {
        myBatisMailEventRepository.updateStatusInBulk(ids, status);
    }
}
