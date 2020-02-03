package io.army.boot;


import com.example.domain.account.Account_;
import com.example.generator.Being;
import com.example.generator.Being_;
import io.army.Session;
import io.army.SessionFactory;
import io.army.criteria.impl.SQLS;
import io.army.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTests.class);

    @Test
    public void openSession() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.generator");
        map.put(Environment.SHOW_SQL, "true");
        SessionFactory sessionFactory = BootstrapTests.builder(map)
                .build();
        try (Session session = sessionFactory.sessionBuilder().openSession()) {
            Being being = new Being();
            session.save(being);

            LOG.info("being:{}", being);
        } catch (Exception e) {
            LOG.debug("", e);
            throw e;
        }
    }

    @Test
    public void singleUpdate() {
        SQLS.update(Account_.T)
                .set(Account_.balance, BigDecimal.ONE)
                .set(Account_.updateTime, LocalDateTime.now())
                .set(Account_.visible,Boolean.TRUE)
                .set(Account_.userId,0L)
                .where(Arrays.asList(Being_.id.add(1L).eq(2L),Being_.visible.eq(true)))
                .orderBy(Account_.id)
                .desc()
                .limit(10)
        ;
    }


}
