package io.army;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;


@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
        DataSourceAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication
                .run(Application.class, args);
    }


}
