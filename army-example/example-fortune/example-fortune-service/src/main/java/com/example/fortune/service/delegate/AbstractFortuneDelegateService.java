package com.example.fortune.service.delegate;

import com.example.domain.EDomain;
import com.example.fortune.service.reactive.FortuneReactiveService;
import com.example.fortune.service.sync.FortuneSyncService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Mono;

import java.util.List;


public abstract class AbstractFortuneDelegateService<S extends FortuneSyncService, R extends FortuneReactiveService>
        implements FortuneReactiveService, ApplicationContextAware, InitializingBean, BeanNameAware {

    protected S syncService;

    protected R reactiveService;

    private ApplicationContext applicationContext;

    private String beanName;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Environment env = this.applicationContext.getEnvironment();
        final String syncProfile = "sync", reactiveProfile = "reactive";
        final String syncBeanName = syncProfile + Character.toUpperCase(this.beanName.charAt(0))
                + this.beanName.substring(1);
        final String reactiveBeanName = reactiveProfile + Character.toUpperCase(this.beanName.charAt(0))
                + this.beanName.substring(1);

        for (String activeProfile : env.getActiveProfiles()) {
            if (reactiveProfile.equals(activeProfile)) {
                this.reactiveService = this.applicationContext.getBean(reactiveBeanName, getReactiveServiceClass());
                break;
            } else if (syncProfile.equals(activeProfile)) {
                this.syncService = this.applicationContext.getBean(syncBeanName, getSyncServiceClass());
                break;
            }
        }

        if (this.reactiveService == null && this.syncService == null) {
            throw new RuntimeException(
                    String.format("not found reactive service[%s] or sync service[%s].", reactiveBeanName, syncBeanName));
        }
    }


    @Override
    public <D extends EDomain> Mono<Boolean> isExists(Class<D> domainClass, Object id) {
        Mono<Boolean> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.isExists(domainClass, id);
        } else {
            mono = Mono.just(this.syncService.isExists(domainClass, id));
        }
        return mono;
    }

    @Override
    public <D extends EDomain> Mono<Boolean> isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue) {
        Mono<Boolean> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.isExistsByUnique(domainClass, uniquePropName, uniqueValue);
        } else {
            mono = Mono.just(this.syncService.isExistsByUnique(domainClass, uniquePropName, uniqueValue));
        }
        return mono;
    }

    @Override
    public <D extends EDomain> Mono<Boolean> isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList) {
        Mono<Boolean> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.isExistsByUnique(domainClass, propNameList, valueList);
        } else {
            mono = Mono.just(this.syncService.isExistsByUnique(domainClass, propNameList, valueList));
        }
        return mono;
    }

    @Override
    public <D extends EDomain> Mono<D> get(Class<D> domainClass, Object id) {
        Mono<D> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.get(domainClass, id);
        } else {
            mono = Mono.justOrEmpty(this.syncService.get(domainClass, id));
        }
        return mono;
    }

    @Override
    public <D extends EDomain> Mono<D> getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue) {
        Mono<D> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.getByUnique(domainClass, uniquePropName, uniqueValue);
        } else {
            mono = Mono.justOrEmpty(this.syncService.getByUnique(domainClass, uniquePropName, uniqueValue));
        }
        return mono;
    }

    @Override
    public <D extends EDomain> Mono<D> getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList) {
        Mono<D> mono;
        if (this.reactiveService != null) {
            mono = this.reactiveService.getByUnique(domainClass, propNameList, valueList);
        } else {
            mono = Mono.justOrEmpty(this.syncService.getByUnique(domainClass, propNameList, valueList));
        }
        return mono;
    }


    protected abstract Class<S> getSyncServiceClass();

    protected abstract Class<R> getReactiveServiceClass();
}
