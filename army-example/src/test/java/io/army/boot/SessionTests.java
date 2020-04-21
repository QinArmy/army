package io.army.boot;


import com.example.domain.account.AccountType;
import com.example.domain.account.Account_;
import com.example.domain.user.User_;
import com.example.generator.Being;
import io.army.Session;
import io.army.SessionFactory;
import io.army.criteria.*;
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
        Update update = SQLS.singleUpdate(Account_.T, map)
                .update(Account_.T, "a")
                .set(Account_.balance, SQLS.field("a", Account_.balance).add(BigDecimal.ONE).brackets())
                .ifSet(this::isUser, Account_.balance, BigDecimal.ZERO)
                .where(
                        SQLS.field("a", Account_.userId)
                                .add(SQLS.constant(1L))
                                .brackets()
                                .multiply(3)
                                .eq(2L)
                )
                .and(SQLS.field("a", Account_.visible).eq(true))
                .and(SQLS.field("a", Account_.createTime).eq(LocalDateTime.now()))
                .asUpdate();

        LOG.info("dml:\n{}", update.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);
    }

    @Test(invocationCount = 10)
    public void objectSQLUpdate() {
       /* final long start = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();
        Update update = ObjectSQLS.updateWithCriteria(InvestAccount_.T, map).as("a")
                .set(InvestAccount_.balance, InvestAccount_.balance.add(BigDecimal.ONE).brackets())
                .ifSet(this::isUser, InvestAccount_.balance, BigDecimal.ZERO)
                .where(InvestAccount_.userId.add(SQLS.constant(1L)).brackets().multiply(3).eq(2L))
                .and(InvestAccount_.visible.eq(true))
                .and(InvestAccount_.createTime.eq(LocalDateTime.now()));

        LOG.info("dml:\n{}", update.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);*/
    }

    @Test(invocationCount = 10)
    public void singleDelete() {
        final long start = System.currentTimeMillis();

        Map<String, Object> criteria = new HashMap<>();

        Delete delete = SQLS.singleDelete(criteria)
                .deleteFrom(Account_.T)
                .where(Account_.id.le(1000L))
                .and(Account_.debt.gt(BigDecimal.ONE))
                .ifAnd(this::isUser, Account_.accountType.eq(AccountType.BALANCE))
                .asDelete();

        LOG.info("dml:\n{}", delete.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);
    }

    @Test(invocationCount = 10)
    public void multiSelect() {
        final long start = System.currentTimeMillis();

        Map<String, Object> criteria = new HashMap<>();

        Select select = SQLS.multiSelect(criteria)
                .select(Distinct.DISTINCT, SQLS.group(Account_.T, "a"))
                .from(Account_.T, "a")
                .join(User_.T, "u").on(Account_.id.eq(User_.id))
                .where(Account_.id.eq(1L))
                .and(Account_.debt.gt(BigDecimal.ZERO))
                .ifGroupBy(this::isUser, Account_.id)
                .having(Account_.id.gt(0L))
                .orderBy(Account_.id)
                .limit(10)
                .lock(LockMode.READ)
                .asSelect();

        LOG.info("dml:\n{}", select.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);

    }


    private boolean isUser(Map<String, Object> map) {
        return true;
    }


}
