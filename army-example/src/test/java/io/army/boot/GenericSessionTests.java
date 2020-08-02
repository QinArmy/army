package io.army.boot;


import io.army.GenericSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

public class GenericSessionTests {

    private static final Logger LOG = LoggerFactory.getLogger(GenericSessionTests.class);

    private static GenericSessionFactory sessionFactory;

    @BeforeClass
    public static void initSessionFactory() {
        sessionFactory = buildSessionFactory();
    }

    public static GenericSessionFactory buildSessionFactory() {
       /* return SessionFactoryBuilder.mockBuilder()
                .sqlDialect(Database.MySQL57)
                .build();*/
        return null;
    }

    @Test
    public void openSession() throws Exception {

    }


    @Test(invocationCount = 10)
    public void singleUpdate() {

    }

    @Test(invocationCount = 10)
    public void objectSQLUpdate() {

    }

    @Test(invocationCount = 10)
    public void singleDelete() {

    }

    @Test(invocationCount = 10)
    public void multiSelect() {


    }


    private boolean isUser(Map<String, Object> map) {
        return true;
    }


}
