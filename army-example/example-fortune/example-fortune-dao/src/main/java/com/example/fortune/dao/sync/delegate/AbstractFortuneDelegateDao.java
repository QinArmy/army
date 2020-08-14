package com.example.fortune.dao.sync.delegate;

import com.example.domain.EDomain;
import com.example.fortune.dao.sync.FortuneSyncDao;
import io.army.dialect.Database;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import java.util.List;

public abstract class AbstractFortuneDelegateDao<T extends FortuneSyncDao>
        implements FortuneSyncDao, InitializingBean, ApplicationContextAware, BeanNameAware {

    protected T dao;

    private String beanName;

    private ApplicationContext applicationContext;

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Environment env = this.applicationContext.getEnvironment();
        Database database = env.getRequiredProperty("fortune.dao.database", Database.class);
        String daoBeanName = Character.toLowerCase(database.name().charAt(0)) + database.name().substring(1)
                + Character.toUpperCase(this.beanName.charAt(0)) + this.beanName.substring(1);
        this.dao = this.applicationContext.getBean(daoBeanName, getDaoClass());
    }

    @Override
    public boolean supportSessionCache() {
        return this.dao.supportSessionCache();
    }

    @Override
    public <D extends EDomain> void save(D domain) {
        this.dao.save(domain);
    }

    @Override
    public <D extends EDomain> boolean isExists(Class<D> domainClass, Object id) {
        return this.dao.isExists(domainClass, id);
    }

    @Override
    public <D extends EDomain> boolean isExists(Class<D> domainClass, Object id, @Nullable Boolean visible) {
        return this.dao.isExists(domainClass, id, visible);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue) {
        return this.dao.isExistsByUnique(domainClass, uniquePropName, uniqueValue);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue, @Nullable Boolean visible) {
        return this.dao.isExistsByUnique(domainClass, uniquePropName, uniqueValue, visible);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return this.dao.isExistsByUnique(domainClass, propNameList, valueList);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList, @Nullable Boolean visible) {
        return this.dao.isExistsByUnique(domainClass, propNameList, valueList, visible);
    }

    @Override
    public <D extends EDomain> D get(Class<D> domainClass, Object id) {
        return this.dao.get(domainClass, id);
    }

    @Override
    public <D extends EDomain> D get(Class<D> domainClass, Object id, @Nullable Boolean visible) {
        return this.dao.get(domainClass, id, visible);
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue) {
        return this.dao.getByUnique(domainClass, uniquePropName, uniqueValue);
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList) {
        return this.dao.getByUnique(domainClass, propNameList, valueList);
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible) {
        return this.dao.getByUnique(domainClass, uniquePropName, uniqueValue, visible);
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible) {
        return this.dao.getByUnique(domainClass, propNameList, valueList, visible);
    }

    protected abstract Class<T> getDaoClass();

}
