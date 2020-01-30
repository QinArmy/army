package io.army.boot;


import io.army.Session;
import io.army.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

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


}
