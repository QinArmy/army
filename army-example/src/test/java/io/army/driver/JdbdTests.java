package io.army.driver;

import io.jdbd.Driver;
import io.jdbd.session.DatabaseSessionFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class JdbdTests {

    private static DatabaseSessionFactory sessionFactory;

    @BeforeClass
    public void beforeClassCreateFactory() {
        final String url;
        final Map<String, Object> map = new HashMap<>();

        url = "jdbd:mysql://localhost:3306/army_bank?factoryWorkerCount=30";

        map.put(Driver.USER, "army_w");
        map.put(Driver.PASSWORD, "army123");


        sessionFactory = Driver.findDriver(url).forDeveloper(url, map);
    }


    @Test
    public void session() {

        Mono.from(sessionFactory.localSession())
                .flatMap(session -> Mono.from(session.close()))
                .block();
    }

}
