package io.army.boot;


import com.example.generator.Being;
import io.army.Session;
import io.army.SessionFactory;
import io.army.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class SessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTests.class);

    @Test
    public void openSession() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.generator");
        map.put(Environment.SHOW_SQL,"true");
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


}
