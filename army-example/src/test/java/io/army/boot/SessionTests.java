package io.army.boot;


import com.example.domain.account.Account_;
import com.example.generator.Being;
import io.army.Session;
import io.army.SessionFactory;
import io.army.criteria.SingleDeleteAble;
import io.army.criteria.UpdateAble;
import io.army.criteria.impl.SQLS;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLS.table;

public class SessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTests.class);

    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void initSessionFactory() {
        sessionFactory = buildSessionFactory();
    }

    public static SessionFactory buildSessionFactory() {
        return SessionFactoryBuilder.mockBuilder()
                .sqlDialect(SQLDialect.MySQL57)
                .build();
    }

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


    @Test(invocationCount = 10)
    public void singleUpdate() {

        final long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        UpdateAble updateAble = SQLS.updateWithCriteria(Account_.T, map).as("a")
                .set(Account_.balance, table("a", Account_.balance).add(BigDecimal.ONE).brackets())
                //.set(Account_.updateTime, LocalDateTime.now())
                // .set(Account_.visible,Boolean.TRUE)
                //.set(Account_.userId,0L)
                .ifSet(this::isUser, Account_.balance, BigDecimal.ZERO)
                .where(table("a", Account_.userId).add(SQLS.constant(1L)).brackets().multiply(3).eq(2L))
                .and(table("a", Account_.visible).eq(true))
                .and(table("a", Account_.createTime).eq(LocalDateTime.now()))
                ;

        LOG.info("sql:\n{}", updateAble.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);
    }

    @Test(invocationCount = 10)
    public void singleDelete() {
        final long start = System.currentTimeMillis();

        Map<String, Object> criteria = new HashMap<>();

        SingleDeleteAble deleteAble = SQLS.deleteWithCriteria(Account_.T, criteria)
                .where(Account_.id.le(1000L))
                .and(Account_.debt.gt(BigDecimal.ONE))
                .orderBy(this::isUser, Account_.id, false)
                .maybeThen(Account_.createTime, false)
                .limit(2);

        LOG.info("sql:\n{}", deleteAble.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);
    }

    private boolean isUser(Map<String, Object> map) {
        return true;
    }


}
