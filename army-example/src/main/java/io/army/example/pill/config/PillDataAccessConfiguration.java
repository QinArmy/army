/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.example.pill.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.army.example.common.BaseService;
import io.army.example.common.SimpleFieldGeneratorFactory;
import io.army.example.pill.service.sync.PillSyncBaseService;
import io.army.example.util.ExampleUtils;
import io.army.generator.FieldGeneratorFactory;
import io.army.session.SyncSessionContext;
import io.army.session.SyncSessionFactory;
import io.army.spring.sync.ArmySyncLocalTransactionManager;
import io.army.spring.sync.ArmySyncSessionFactoryBean;
import io.army.spring.sync.DruidDataSourceUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
        final Properties properties = new Properties();
        if (ExampleUtils.isMyLocal()) {
            properties.put("sslMode", "DISABLED");
        }
        return DruidDataSourceUtils.createDataSource(this.env, properties, "pill", "primary");
    }

    @Bean
    public FieldGeneratorFactory pillFieldGeneratorFactory() {
        return new SimpleFieldGeneratorFactory();
    }

    @Bean
    public ArmySyncSessionFactoryBean pillSyncSessionFactory(@Qualifier("pillDataSource") DataSource dataSource) {
        final List<String> packageList = new ArrayList<>(2);
        packageList.add("io.army.example.pill.domain");
        packageList.add("io.army.example.dialect");

        final ArmySyncSessionFactoryBean factoryBean = ArmySyncSessionFactoryBean.create();

        factoryBean
                .setFactoryName("pill")
                .setDataSource(dataSource)
                .setPackagesToScan(packageList)
                .setFieldGeneratorFactory(pillFieldGeneratorFactory());

        return factoryBean;
    }


    @Bean
    public SyncSessionContext pillSyncSessionContext(
            @Qualifier("pillSyncTransactionManager") ArmySyncLocalTransactionManager manager) {
        return manager.getSessionContext();
    }

    @Bean(PillSyncBaseService.TX_MANAGER)
    public ArmySyncLocalTransactionManager pillSyncTransactionManager(
            @Qualifier("pillSyncSessionFactory") SyncSessionFactory sessionFactory) {

        final ArmySyncLocalTransactionManager manager = ArmySyncLocalTransactionManager.create(sessionFactory);
        manager.setNestedTransactionAllowed(true);

        return manager.setUseDataSourceTimeout(true)
                .setUseTransactionName(true)
                .setUseTransactionLabel(true)
                .setPseudoTransactionAllowed(true);
    }


}
