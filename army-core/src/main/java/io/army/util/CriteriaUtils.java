package io.army.util;

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

    public static Visible convertVisible(@Nullable Boolean visible) {
        Visible v;
        if (visible == null) {
            v = Visible.BOTH;
        } else if (Boolean.TRUE.equals(visible)) {
            v = Visible.ONLY_VISIBLE;
        } else {
            v = Visible.ONLY_NON_VISIBLE;
        }
        return v;
    }

    public static <T extends IDomain> Select createExistsById(Class<T> domainClass, Object id) {
        return createExistsById(TableMetaFactory.getTableMeta(domainClass), id);
    }

    public static <T extends IDomain> Select createExistsById(TableMeta<T> tableMeta, Object id) {
        return SQLS.multiSelect()
                .select(tableMeta.primaryKey())
                .from(tableMeta, "t")
                .where(tableMeta.primaryKey().eq(id))
                .asSelect();
    }

    public static <T extends IDomain> Select createSelectIdByUnique(Class<T> domainClass, List<String> propNameList
            , List<Object> valueList) {
        return createSelectIdByUnique(TableMetaFactory.getTableMeta(domainClass), propNameList, valueList);
    }

    public static <T extends IDomain> Select createSelectIdByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        return SQLS.multiSelect()
                .select(tableMeta.primaryKey())
                .from(tableMeta, "t")
                .where(createPredicateList(tableMeta, propNameList, valueList))
                .limit(2)
                .asSelect();
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


    public static <T extends IDomain> Select createSelectDomainByUnique(Class<T> domainClass, List<String> propNameList
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
