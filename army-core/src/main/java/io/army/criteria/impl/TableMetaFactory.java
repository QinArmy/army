package io.army.criteria.impl;

import io.army.domain.IDomain;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.Set;

public abstract class TableMetaFactory {

    public static <T extends IDomain> TableMeta<T> createSimpleTableMeta(Class<T> entityClass) {
        return DefaultTableMeta.createTableInstance(entityClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> createParentTableMta(Class<T> entityClass) {
        return DefaultTableMeta.createParentInstance(entityClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> createChildTableMeta(ParentTableMeta<? super T> parentTableMeta
            , Class<T> entityClass) {
        return DefaultTableMeta.createChildInstance(parentTableMeta, entityClass);
    }

    public static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> entityClass) throws IllegalArgumentException {
        return DefaultTableMeta.getTableMeta(entityClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(Class<T> entityClass)
            throws IllegalArgumentException {
        return DefaultTableMeta.getParentTableMeta(entityClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(Class<T> entityClass)
            throws IllegalArgumentException {
        return DefaultTableMeta.getChildTableMeta(entityClass);
    }

    public static Set<FieldMeta<?, ?>> codecFieldMetaSet() {
        return DefaultFieldMeta.codecFieldMetaSet();
    }

    public static void cleanCache() {
        if (TableMetaUtils.discriminatorCodeMap != null) {
            TableMetaUtils.discriminatorCodeMap.clear();
            TableMetaUtils.discriminatorCodeMap = null;
        }
    }

}
