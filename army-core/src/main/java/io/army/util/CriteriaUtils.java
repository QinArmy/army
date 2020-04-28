package io.army.util;

import io.army.GenericSession;
import io.army.NonUniqueException;
import io.army.criteria.IPredicate;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.TableMetaFactory;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CriteriaUtils {

    protected CriteriaUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T extends IDomain> Insert createSingleInsert(Class<T> domainClass, T domain) {
        TableMeta<T> tableMeta = TableMetaFactory.getTableMeta(domainClass);
        return SQLS.insert(tableMeta)
                .insertInto(tableMeta)
                .value(domain)
                .asInsert();
    }

    public static <T extends IDomain> Insert createMultiInsert(Class<T> domainClass, List<T> domainList) {
        TableMeta<T> tableMeta = TableMetaFactory.getTableMeta(domainClass);
        return SQLS.insert(tableMeta)
                .insertInto(tableMeta)
                .values(domainList)
                .asInsert();
    }

    @Nullable
    public static <T extends IDomain> T getByUnique(GenericSession genericSession, Class<T> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return getByUnique(genericSession, domainClass, propNameList, valueList, Visible.ONLY_VISIBLE);
    }

    @Nullable
    public static <T extends IDomain> T getByUnique(GenericSession genericSession, Class<T> domainClass, List<String> propNameList
            , List<Object> valueList, Visible visible) {

        Select select = createSelectByUnique(domainClass, propNameList, valueList);
        List<T> list = genericSession.select(select, visible);

        T domain;
        if (list.size() == 1) {
            domain = list.get(0);
        } else if (list.size() > 1) {
            throw new NonUniqueException("propNameList[%s] don't select unique domain.", propNameList);
        } else {
            domain = null;
        }
        return domain;
    }

    public static <T extends IDomain> Select createSelectByUnique(Class<T> domainClass, List<String> propNameList
            , List<Object> valueList) {
        TableMeta<T> tableMeta = TableMetaFactory.getTableMeta(domainClass);
        Select select;
        if (tableMeta instanceof ChildTableMeta) {
            ChildTableMeta<T> childMeta = (ChildTableMeta<T>) tableMeta;
            ParentTableMeta<?> parentMeta = childMeta.parentMeta();

            select = SQLS.multiSelect()
                    .select(Arrays.asList(SQLS.group(parentMeta, "p"), SQLS.group(childMeta, "c")))
                    .from(childMeta, "c")
                    .join(parentMeta, "p").on(parentMeta.primaryKey().eq(childMeta.primaryKey()))
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asSelect();

        } else {
            select = SQLS.multiSelect()
                    .select(SQLS.group(tableMeta, "d"))
                    .from(tableMeta, "d")
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asSelect();
        }
        return select;
    }

    protected static <T extends IDomain> List<IPredicate> createPredicateList(TableMeta<T> tableMeta
            , List<String> propNameList, List<Object> valueList) {

        Assert.isTrue(propNameList.size() == valueList.size(), "propNameList size and valueList size not match.");

        ParentTableMeta<?> parentMeta = tableMeta.parentMeta();

        List<IPredicate> list = new ArrayList<>(propNameList.size());
        final int size = propNameList.size();
        String propName;
        for (int i = 0; i < size; i++) {
            propName = propNameList.get(i);
            if (TableMeta.ID.equals(propName)) {
                list.add(tableMeta.primaryKey().eq(valueList.get(i)));
            } else if (tableMeta.isMappingProp(propName)) {
                list.add(tableMeta.getField(propName).eq(valueList.get(i)));
            } else if (parentMeta != null && parentMeta.isMappingProp(propName)) {
                list.add(parentMeta.getField(propName).eq(valueList.get(i)));
            } else {
                throw new IllegalArgumentException(String.format("propName[%s] isn't prop of TableMeta[%s]"
                        , propName, tableMeta));
            }
        }
        return list;
    }
}
