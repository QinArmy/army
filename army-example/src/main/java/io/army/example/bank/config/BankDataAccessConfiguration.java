package io.army.example.bank.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.example.util.ExampleUtils;
import io.army.generator.FieldGeneratorFactory;
import io.army.spring.sync.ArmySyncLocalTransactionManager;
import io.army.spring.sync.ArmySyncSessionFactoryBean;
import io.army.spring.sync.DruidDataSourceUtils;
import io.army.sync.SyncSessionContext;
import io.army.sync.SyncSessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.Properties;

@Configuration
public class BankDataAccessConfiguration implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource bankDataSource() {
        final Properties properties = new Properties();
        if (ExampleUtils.isMyLocal()) {
            properties.put("sslMode", "DISABLED");
        }
        return DruidDataSourceUtils.createDataSource(this.env, properties, "bank", "primary");
    }

    @Bean
    public FieldGeneratorFactory bankFieldGeneratorFactory() {
        return new SimpleFieldGeneratorFactory();
    }


    @Bean
    public ArmySyncSessionFactoryBean bankSyncSessionFactory(@Qualifier("bankDataSource") DataSource dataSource) {
        final ArmySyncSessionFactoryBean factoryBean;
        factoryBean = ArmySyncSessionFactoryBean.create();

        factoryBean.setFactoryName("bank")
                .setDataSource(dataSource)
                .setPackagesToScan(Collections.singletonList("io.army.example.bank.domain"))
                .setFieldGeneratorFactoryBean("bankFieldGeneratorFactory");

        return factoryBean;
    }

    @Bean
    public SyncSessionContext bankSyncSessionContext(
            @Qualifier("bankSyncTransactionManager") ArmySyncLocalTransactionManager manager) {
        return manager.getSessionContext();
    }

    @Bean(BankSyncBaseService.TX_MANAGER)
    public ArmySyncLocalTransactionManager bankSyncTransactionManager(
            @Qualifier("bankSyncSessionFactory") SyncSessionFactory sessionFactory) {
        final ArmySyncLocalTransactionManager manager;
        manager = ArmySyncLocalTransactionManager.create(sessionFactory);
        manager.setNestedTransactionAllowed(true);

        return manager.setPseudoTransactionAllowed(true)
                .setUseTransactionLabel(true)
                .setUseDataSourceTimeout(true);


    }


}
