package com.example.fortune.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.fortune.service.sync.FortuneBaseSyncService;
import io.army.ShardingMode;
import io.army.boot.sync.LocalSessionFactoryBean;
import io.army.datasource.DataSourceRole;
import io.army.datasource.sync.DruidDataSourceUtils;
import io.army.datasource.sync.PrimarySecondaryRoutingDataSource;
import io.army.sync.ProxySession;
import io.army.sync.SessionFactory;
import io.army.tx.sync.ArmyTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.EnumMap;
import java.util.Map;

@Configuration
@Profile({"sync", "no_sharding"})
public class FortuneSyncPersistenceConfig implements EnvironmentAware {

    private static final String TAG = "fortune";

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource fortunePrimaryDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, TAG, DataSourceRole.PRIMARY);
    }

    @Bean(destroyMethod = "close")
    public DruidDataSource fortuneSecondaryDataSource() {
        return DruidDataSourceUtils.createDataSource(this.env, TAG, DataSourceRole.SECONDARY);
    }

    @Bean
    public DataSource fortuneRoutingDataSource() {
        Map<DataSourceRole, DataSource> map = new EnumMap<>(DataSourceRole.class);

        map.put(DataSourceRole.PRIMARY, fortunePrimaryDataSource());
        map.put(DataSourceRole.SECONDARY, fortuneSecondaryDataSource());

        PrimarySecondaryRoutingDataSource ds = new PrimarySecondaryRoutingDataSource(map);
        ds.setTimeoutBoundary(
                env.getProperty(String.format("spring.datasource.%s.timeout.boundary", TAG), Integer.class, 10));
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean fortuneSessionFactory() {
        return new LocalSessionFactoryBean()
                .setShardingMode(ShardingMode.NO_SHARDING)
                .setDataSourceBeanName("fortuneRoutingDataSource");
    }

    @Bean(name = FortuneBaseSyncService.TX_MANAGER)
    public ArmyTransactionManager fortuneTransactionManager(
            @Qualifier("fortuneSessionFactory") SessionFactory sessionFactory) {
        return new ArmyTransactionManager(sessionFactory);
    }

    @Bean
    public ProxySession fortuneProxySession(@Qualifier("fortuneSessionFactory") SessionFactory sessionFactory) {
        return sessionFactory.proxySession();
    }

}
