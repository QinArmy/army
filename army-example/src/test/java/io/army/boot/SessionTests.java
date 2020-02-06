package io.army.boot;


import com.example.domain.account.Account_;
import com.example.generator.Being;
import io.army.Session;
import io.army.SessionFactory;
import io.army.criteria.SingleUpdateAble;
import io.army.criteria.impl.SQLS;
import io.army.dialect.SQLDialect;
import io.army.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLS.table;

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
     SingleUpdateAble updateAble =  SQLS.update(Account_.T).as("a")
                .set(Account_.balance, table("a",Account_.balance).add(BigDecimal.ONE).brackets())
                //.set(Account_.updateTime, LocalDateTime.now())
               // .set(Account_.visible,Boolean.TRUE)
                //.set(Account_.userId,0L)
                .where(Arrays.asList(table("a",Account_.userId).add(1L).eq(2L), table("a",Account_.visible).eq(true)))
                .orderBy(Account_.id,false).then(Account_.createTime,true)
                .limit(10)
        ;

      LOG.info("sql:\n{}",updateAble.debugSQL(SQLDialect.MySQL57));
    }


}
