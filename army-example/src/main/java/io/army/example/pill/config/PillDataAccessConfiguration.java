package io.army.example.pill.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.boot.sync.LocalSessionFactoryBean;
import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import io.army.example.common.BaseService;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.example.pill.service.sync.PillSyncBaseService;
import io.army.generator.FieldGeneratorFactory;
import io.army.sync.SyncLocalSessionFactory;
import io.army.tx.sync.ArmyTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Profile(BaseService.SYNC)
public class PillDataAccessConfiguration implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment env) {
        this.env = env;
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource pillDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, "pill", DataSourceRole.PRIMARY);
    }

    @Bean
    public FieldGeneratorFactory pillFieldGeneratorFactory() {
        return new SimpleFieldGeneratorFactory();
    }

    @Bean
    public LocalSessionFactoryBean pillSyncSessionFactory(@Qualifier("pillDataSource") DataSource dataSource) {
        final List<String> packageList = new ArrayList<>(2);
        packageList.add("io.army.example.pill.domain");
        packageList.add("io.army.example.dialect");

        return new LocalSessionFactoryBean()
                .setFactoryName("pill")
                .setDataSource(dataSource)
                .setPackagesToScan(packageList)
                .setFieldGeneratorFactory(pillFieldGeneratorFactory());
    }

    @Bean(PillSyncBaseService.TX_MANAGER)
    public ArmyTransactionManager pillSyncTransactionManager(
            @Qualifier("pillSyncSessionFactory") SyncLocalSessionFactory sessionFactory) {
        ArmyTransactionManager manager = new ArmyTransactionManager(sessionFactory);
        manager.setWrapSession(false);
        manager.setNestedTransactionAllowed(true);
        return manager;
    }


}
