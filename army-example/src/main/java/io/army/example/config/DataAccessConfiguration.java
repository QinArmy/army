package io.army.example.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.boot.sync.LocalSessionFactoryBean;
import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataAccessConfiguration implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource exampleDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, "example", DataSourceRole.PRIMARY);
    }

    @Bean
    public LocalSessionFactoryBean armyExampleSessionFactory(@Qualifier("exampleDataSource") DataSource dataSource) {
        final List<String> packageList = new ArrayList<>();
        packageList.add("io.army.example.domain");
        packageList.add("io.army.dialect.mysql");

        return new LocalSessionFactoryBean()
                .setFactoryName("example")
                .setDataSource(dataSource)
                .setPackagesToScan(packageList);
    }


}
