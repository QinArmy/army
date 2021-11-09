package io.army.util;

import io.army.criteria.IPredicate;
import io.army.criteria.Select;
import io.army.criteria.impl.Sqls;
import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class CriteriaUtils {

    protected CriteriaUtils() {
        throw new UnsupportedOperationException();
    }




    public static <T extends IDomain> Select createSelectIdByUnique(TableMeta<T> tableMeta, List<String> propNameList
            , List<Object> valueList) {
        Select select;
        if (tableMeta instanceof ChildTableMeta) {
            final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

            select = Sqls.multiSelect()
                    .select(tableMeta.id())
                    .from(childMeta, "c") // small table first
                    .join(parentMeta, "p").on(childMeta.id().equal(parentMeta.id()))
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asQuery();
        } else {
            select = Sqls.multiSelect()
                    .select(tableMeta.id())
                    .from(tableMeta, "t")
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asQuery();
        }
        return select;
    }

    public static <T extends IDomain> Select createSelectDomainById(final TableMeta<T> tableMeta, Object id) {
        Select select;
        if (tableMeta instanceof ChildTableMeta) {
            final ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

            select = Sqls.multiSelect()
                    .select(Sqls.childGroup(childMeta, "p", "c"))
                    .from(childMeta, "c") // small table first
                    .join(parentMeta, "p").on(childMeta.id().equal(parentMeta.id()))
                    .where(childMeta.id().equal(id))
                    .asQuery();
        } else {
            select = Sqls.multiSelect()
                    .select(Sqls.group(tableMeta, "t"))
                    .from(tableMeta, "t")
                    .where(tableMeta.id().equal(id))
                    .asQuery();
        }
        return select;
    }

    public static <T extends IDomain> Select createSelectDomainByUnique(TableMeta<T> tableMeta
            , List<String> propNameList, List<Object> valueList) {
        Select select;
        if (tableMeta instanceof ChildTableMeta) {
            final ChildTableMeta<T> childMeta = (ChildTableMeta<T>) tableMeta;
            final ParentTableMeta<?> parentMeta = childMeta.parentMeta();

            select = Sqls.multiSelect()
                    .select(Arrays.asList(Sqls.group(parentMeta, "p"), Sqls.group(childMeta, "c")))
                    .from(childMeta, "c")
                    .join(parentMeta, "p").on(parentMeta.id().equal(childMeta.id()))
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asQuery();

        } else {
            select = Sqls.multiSelect()
                    .select(Sqls.group(tableMeta, "d"))
                    .from(tableMeta, "d")
                    .where(createPredicateList(tableMeta, propNameList, valueList))
                    .limit(2)
                    .asQuery();
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
            if (_MetaBridge.ID.equals(propName)) {
                list.add(tableMeta.id().equal(valueList.get(i)));
            } else if (tableMeta.mappingField(propName)) {
                list.add(tableMeta.getField(propName).equal(valueList.get(i)));
            } else if (parentMeta != null && parentMeta.mappingField(propName)) {
                list.add(parentMeta.getField(propName).equal(valueList.get(i)));
            } else {
                throw new IllegalArgumentException(String.format("propName[%s] isn't prop of TableMeta[%s]"
                        , propName, tableMeta));
            }
        }
        return list;
    }
}
