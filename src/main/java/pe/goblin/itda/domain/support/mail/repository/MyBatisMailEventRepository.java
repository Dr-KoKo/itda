package pe.goblin.itda.domain.support.mail.repository;

import org.apache.ibatis.annotations.Mapper;
import pe.goblin.itda.domain.support.mail.dto.MailEvent;
import pe.goblin.itda.domain.support.mail.dto.Status;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MyBatisMailEventRepository {
    void save(MailEvent mailEvent);

    void saveAll(Iterable<MailEvent> mailEvent);

    Optional<MailEvent> findById(Long id);

    List<MailEvent> findAllById(Iterable<Long> ids);

    List<MailEvent> findAllByStatus(Status status, int limit);

    int updateStatus(Long id, Status status);

    int updateStatusInBulk(List<Long> ids, Status status);
}
