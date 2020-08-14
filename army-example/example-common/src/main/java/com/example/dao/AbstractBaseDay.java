package com.example.dao;

import com.example.domain.EDomain;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLS;
import io.army.meta.TableMeta;
import io.army.sync.GenericSyncProxySession;
import io.army.util.CriteriaUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import java.util.Collections;
import java.util.List;

public abstract class AbstractBaseDay implements BaseDao, EnvironmentAware
        , InitializingBean {

    protected GenericSyncProxySession session;

    protected Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
    }


    @Override
    public boolean supportSessionCache() {
        return session.sessionFactory().supportSessionCache();
    }

    @Override
    public <D extends EDomain> void save(D domain) {
        @SuppressWarnings("unchecked")
        TableMeta<D> tableMeta = obtainTableMeta((Class<D>) domain.getClass());
        Insert insert = SQLS.multiInsert(tableMeta)
                .insertInto(tableMeta)
                .value(domain)
                .asInsert();
        session.valueInsert(insert, Visible.ONLY_VISIBLE);
    }


    @Override
    public <D extends EDomain> boolean isExists(Class<D> domainClass, Object id) {
        return isExists(domainClass, id, Boolean.TRUE);
    }

    @Override
    public <D extends EDomain> boolean isExists(Class<D> domainClass, Object id, @Nullable Boolean visible) {
        TableMeta<D> tableMeta = obtainTableMeta(domainClass);
        Select select = SQLS.multiSelect()
                .select(tableMeta.id())
                .from(tableMeta, "t")
                .where(tableMeta.id().equal(id))
                .asSelect();
        return session.selectOne(select, tableMeta.id().javaType(), Visible.resolve(visible)) != null;
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue) {
        return isExistsByUnique(domainClass, uniquePropName, uniqueValue, Boolean.TRUE);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, String uniquePropName
            , Object uniqueValue, @Nullable Boolean visible) {
        return isExistsByUnique(domainClass, Collections.singletonList(uniquePropName)
                , Collections.singletonList(uniqueValue), visible);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return isExistsByUnique(domainClass, propNameList, valueList, Boolean.TRUE);
    }

    @Override
    public <D extends EDomain> boolean isExistsByUnique(Class<D> domainClass, List<String> propNameList
            , List<Object> valueList, @Nullable Boolean visible) {
        TableMeta<D> tableMeta = obtainTableMeta(domainClass);
        Select select = CriteriaUtils.createSelectIdByUnique(tableMeta, propNameList, valueList);
        return session.selectOne(select, tableMeta.id().javaType(), Visible.resolve(visible)) != null;
    }

    @Override
    public <D extends EDomain> D get(Class<D> domainClass, Object id) {
        return session.get(obtainTableMeta(domainClass), id, Visible.ONLY_VISIBLE);
    }

    @Override
    public <D extends EDomain> D get(Class<D> domainClass, Object id, @Nullable Boolean visible) {
        return session.get(obtainTableMeta(domainClass), id, Visible.resolve(visible));
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue) {
        return getByUnique(domainClass, uniquePropName, uniqueValue, Boolean.TRUE);
    }

    @Override
    public <D extends EDomain> D getByUnique(Class<D> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible) {
        return getByUnique(domainClass, Collections.singletonList(uniquePropName)
                , Collections.singletonList(uniqueValue), visible);
    }

    @Override
    @Nullable
    public <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList) {
        return getByUnique(domainClass, propNameList, valueList, Boolean.TRUE);
    }

    @Override
    @Nullable
    public <D extends EDomain> D getByUnique(Class<D> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible) {
        return this.session.getByUnique(obtainTableMeta(domainClass), propNameList, valueList, Visible.resolve(visible));
    }

    /*################################## blow protected method ##################################*/

    protected <D extends EDomain> TableMeta<D> obtainTableMeta(Class<D> domainClass) {
        TableMeta<D> tableMeta = session.tableMeta(domainClass);
        if (tableMeta == null) {
            throw new IllegalArgumentException(String.format("not find TableMeta for Class[%s]"
                    , domainClass.getName()));
        }
        return tableMeta;
    }

}
