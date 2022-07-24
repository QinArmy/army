package io.army.example.pill.service.reactive;


import io.army.example.common.BaseService;
import io.army.example.common.Domain;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("baseServiceDispatcher")
@Profile(BaseService.REACTIVE)
public class BaseServiceImpl implements BaseService, InitializingBean, ApplicationContextAware {

    public static final String TX_MANAGER = "exampleReactiveTransactionManager";

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final ApplicationContext ctx = this.applicationContext;
        final Environment env = ctx.getEnvironment();

    }


    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <T extends Domain> Mono<T> get(Class<T> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public <T extends Domain> Mono<Void> save(T domain) {
        throw new UnsupportedOperationException();
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public <T extends Domain> Mono<T> findById(Class<T> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Mono<Map<String, Object>> findByIdAsMap(Class<?> domainClass, Object id) {
        throw new UnsupportedOperationException();
    }


}
