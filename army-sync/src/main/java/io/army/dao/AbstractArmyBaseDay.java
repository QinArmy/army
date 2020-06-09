package io.army.dao;

import io.army.ProxySession;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLS;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.CriteriaUtils;

import java.util.Collections;
import java.util.List;

public abstract class AbstractArmyBaseDay implements ArmyBaseDao {

    protected ProxySession session;

    @Override
    public boolean supportSessionCache() {
        return session.sessionFactory().supportSessionCache();
    }

    @Override
    public <T extends IDomain> void save(T domain) {
        @SuppressWarnings("unchecked")
        TableMeta<T> tableMeta = obtainTableMeta((Class<T>) domain.getClass());
        Insert insert = SQLS.multiInsert(tableMeta)
                .insertInto(tableMeta)
                .value(domain)
                .asInsert();
        session.insert(insert, Visible.ONLY_VISIBLE);
    }


    @Override
    public <T extends IDomain> void multiSave(Class<T> domainClass, List<T> domainList) {
        TableMeta<T> tableMeta = obtainTableMeta(domainClass);
        Insert insert = SQLS.multiInsert(tableMeta)
                .insertInto(tableMeta)
                .values(domainList)
                .asInsert();
        session.insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T extends IDomain> void batchSave(Class<T> domainClass, List<T> domainList) {
        TableMeta<T> tableMeta = obtainTableMeta(domainClass);
        Insert insert = SQLS.batchInsert(tableMeta)
                .insertInto(tableMeta)
                .values(domainList)
                .asInsert();
        session.insert(insert, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T extends IDomain> boolean isExists(Class<T> domainClass, Object id) {
        return isExists(domainClass, id, Boolean.TRUE);
    }

    @Override
    public <T extends IDomain> boolean isExists(Class<T> domainClass, Object id, @Nullable Boolean visible) {
        TableMeta<T> tableMeta = obtainTableMeta(domainClass);
        Select select = SQLS.multiSelect()
                .select(tableMeta.id())
                .from(tableMeta, "t")
                .where(tableMeta.id().equal(id))
                .asSelect();
        return session.selectOne(select, tableMeta.id().javaType(), Visible.resolve(visible)) != null;
    }

    @Override
    public <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, String uniquePropName
            , Object uniqueValue) {
        return isExistsByUnique(domainClass, uniquePropName, uniqueValue, Boolean.TRUE);
    }

    @Override
    public <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, String uniquePropName
            , Object uniqueValue, @Nullable Boolean visible) {
        return isExistsByUnique(domainClass, Collections.singletonList(uniquePropName)
                , Collections.singletonList(uniqueValue), visible);
    }

    @Override
    public <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return isExistsByUnique(domainClass, propNameList, valueList, Boolean.TRUE);
    }

    @Override
    public <T extends IDomain> boolean isExistsByUnique(Class<T> domainClass, List<String> propNameList
            , List<Object> valueList, @Nullable Boolean visible) {
        TableMeta<T> tableMeta = obtainTableMeta(domainClass);
        Select select = CriteriaUtils.createSelectIdByUnique(tableMeta, propNameList, valueList);
        return session.selectOne(select, tableMeta.id().javaType(), Visible.resolve(visible)) != null;
    }

    @Override
    public <T extends IDomain> T get(Class<T> domainClass, Object id) {
        return session.get(obtainTableMeta(domainClass), id, Visible.ONLY_VISIBLE);
    }

    @Override
    public <T extends IDomain> T get(Class<T> domainClass, Object id, @Nullable Boolean visible) {
        return session.get(obtainTableMeta(domainClass), id, Visible.resolve(visible));
    }

    @Override
    public <T extends IDomain> T getByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue) {
        return getByUnique(domainClass, uniquePropName, uniqueValue, Boolean.TRUE);
    }

    @Override
    public <T extends IDomain> T getByUnique(Class<T> domainClass, String uniquePropName, Object uniqueValue
            , @Nullable Boolean visible) {
        return getByUnique(domainClass, Collections.singletonList(uniquePropName)
                , Collections.singletonList(uniqueValue), visible);
    }

    @Override
    @Nullable
    public <T extends IDomain> T getByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList) {
        return getByUnique(domainClass, propNameList, valueList, Boolean.TRUE);
    }

    @Override
    @Nullable
    public <T extends IDomain> T getByUnique(Class<T> domainClass, List<String> propNameList, List<Object> valueList
            , @Nullable Boolean visible) {
        return session.getByUnique(obtainTableMeta(domainClass), propNameList, valueList, Visible.resolve(visible));
    }

    /*################################## blow protected method ##################################*/

    protected <T extends IDomain> TableMeta<T> obtainTableMeta(Class<T> domainClass) {
        TableMeta<T> tableMeta = session.tableMeta(domainClass);
        if (tableMeta == null) {
            throw new IllegalArgumentException(String.format("not find TableMeta for Class[%s]"
                    , domainClass.getName()));
        }
        return tableMeta;
    }
}
