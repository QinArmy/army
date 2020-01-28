package io.army.boot;


import io.army.Session;
import io.army.SessionFactory;
import io.army.dialect.SQLWrapper;
import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.BooleanMapping;
import io.army.meta.mapping.LocalDateMapping;
import io.army.meta.mapping.LongMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTests.class);

    @Test
    public void openSession() throws Exception {
        SessionFactory sessionFactory = BootstrapTests.builder()
                .packagesToScan("com.example.domain.**")
                .build();
        try (Session session = sessionFactory.sessionBuilder().openSession()) {
            LOG.info("session:{}", session);
        } catch (Exception e) {
            LOG.debug("", e);
            throw e;
        }
    }

    @Test
    public void printInsertWrapper() {
        List<ParamWrapper> list= new ArrayList<>(3);

        list.add(ParamWrapper.build(LongMapping.INSTANCE,1L));
        list.add(ParamWrapper.build(LocalDateMapping.INSTANCE, LocalDateTime.now()));
        list.add(ParamWrapper.build(BooleanMapping.INSTANCE, Boolean.TRUE));

        SQLWrapper wrapper = SQLWrapper.build(
                "INSERT INTO a_account(id,create_time,visible) value(?,?,?)"
                , list);
        LOG.info("InsertWrapper:{}", wrapper);

        System.out.println(System.currentTimeMillis());
    }

}
