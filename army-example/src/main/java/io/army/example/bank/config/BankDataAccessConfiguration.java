package io.army.example.bank.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.boot.sync.LocalSessionFactoryBean;
import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.generator.FieldGeneratorFactory;
import io.army.tx.sync.ArmyLocalTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
public class BankDataAccessConfiguration implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource bankDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, "bank", DataSourceRole.PRIMARY);
    }

    @Bean
    public FieldGeneratorFactory bankFieldGeneratorFactory() {
        return new SimpleFieldGeneratorFactory();
    }

    @Bean
    public LocalSessionFactoryBean bankSyncSessionFactory(@Qualifier("bankDataSource") DataSource dataSource) {
        return new LocalSessionFactoryBean()
                .setFactoryName("bank")
                .setDataSource(dataSource)
                .setPackagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .setFieldGeneratorFactoryBean("bankFieldGeneratorFactory");
    }

    @Bean(BankSyncBaseService.TX_MANAGER)
    public ArmyLocalTransactionManager bankSyncTransactionManager(
            @Qualifier("bankSyncSessionFactory") SyncLocalSessionFactory sessionFactory) {
        ArmyLocalTransactionManager manager = new ArmyLocalTransactionManager(sessionFactory);
        manager.setWrapSession(false);
        manager.setNestedTransactionAllowed(true);
        return manager;

    }


}
