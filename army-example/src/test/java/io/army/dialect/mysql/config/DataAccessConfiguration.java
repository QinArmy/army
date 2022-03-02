package io.army.dialect.mysql.config;

import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataAccessConfiguration implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dialectPrimaryDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, "dialect", DataSourceRole.PRIMARY);
    }


}
