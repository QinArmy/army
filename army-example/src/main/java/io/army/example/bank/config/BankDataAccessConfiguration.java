package io.army.example.bank.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.generator.FieldGeneratorFactory;
import io.army.spring.ArmySessionFactoryBeanSupport;
import io.army.spring.sync.ArmySyncLocalTransactionManager;
import io.army.spring.sync.ArmySyncSessionFactoryBean;
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
    public ArmySessionFactoryBeanSupport bankSyncSessionFactory(@Qualifier("bankDataSource") DataSource dataSource) {
        final ArmySyncSessionFactoryBean factoryBean;
        factoryBean = new ArmySyncSessionFactoryBean();

        factoryBean.setFactoryName("bank")
                .setDataSource(dataSource)
                .setPackagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .setFieldGeneratorFactoryBean("bankFieldGeneratorFactory");

        return factoryBean;
    }

    @Bean(BankSyncBaseService.TX_MANAGER)
    public ArmySyncLocalTransactionManager bankSyncTransactionManager(
            @Qualifier("bankSyncSessionFactory") SyncLocalSessionFactory sessionFactory) {
        ArmySyncLocalTransactionManager manager = new ArmySyncLocalTransactionManager(sessionFactory);
        manager.setWrapSession(false);
        manager.setNestedTransactionAllowed(true);
        return manager;

    }


}
