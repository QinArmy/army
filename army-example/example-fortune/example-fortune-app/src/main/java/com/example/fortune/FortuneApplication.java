package com.example.fortune;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class
        , HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class FortuneApplication {


    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(FortuneApplication.class);

        builder.web(WebApplicationType.REACTIVE);
        builder.build(args)
                .run();
    }

}
