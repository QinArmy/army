package io.army.boot;


import com.example.domain.account.Account_;
import com.example.generator.Being;
import io.army.Session;
import io.army.SessionFactory;
import io.army.criteria.SingleUpdateAble;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLS;
import io.army.dialect.SQLDialect;
import io.army.dialect.SQLWrapper;
import io.army.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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


    @Test(invocationCount = 1000)
    public void singleUpdate() {
        final long start = System.currentTimeMillis();

        SingleUpdateAble updateAble = SQLS.update(Account_.T).as("a")
                .set(Account_.balance, table("a", Account_.balance).add(BigDecimal.ONE).brackets())
                //.set(Account_.updateTime, LocalDateTime.now())
                // .set(Account_.visible,Boolean.TRUE)
                //.set(Account_.userId,0L)
                .where(Arrays.asList(table("a", Account_.userId).add(SQLS.constant(1L)).brackets().multiply(3).eq(2L), table("a", Account_.visible).eq(true)))
                .orderBy(Account_.id, false).then(Account_.createTime, true)
                .limit(0);

        LOG.info("sql:\n{}", updateAble.debugSQL(SQLDialect.MySQL57));
        LOG.info("cost:{}", System.currentTimeMillis() - start);
    }


}
