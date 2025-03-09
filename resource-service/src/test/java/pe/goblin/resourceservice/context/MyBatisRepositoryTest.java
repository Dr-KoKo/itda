package pe.goblin.resourceservice.context;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;

@MybatisTest
@ActiveProfiles("test")
public abstract class MyBatisRepositoryTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterTransaction
    void tearDown() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from mail_event", Integer.class);
        if (count != null && count > 0) {
            throw new RuntimeException("data is not clean up");
        }
    }
}
