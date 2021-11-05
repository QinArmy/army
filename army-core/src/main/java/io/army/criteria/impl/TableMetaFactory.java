package io.army.criteria.impl;

import io.army.asm.TableMetaLoadException;
import io.army.domain.IDomain;
import io.army.meta.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class TableMetaFactory {

    private TableMetaFactory() {
        throw new UnsupportedOperationException();
    }

    public static <T extends IDomain> TableMeta<T> getTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getTableMeta(domainClass);
    }

    public static <T extends IDomain> SimpleTableMeta<T> getSimpleTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getSimpleTableMeta(domainClass);
    }

    public static <T extends IDomain> ParentTableMeta<T> getParentTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getParentTableMeta(domainClass);
    }

    public static <T extends IDomain> ChildTableMeta<T> getChildTableMeta(Class<T> domainClass) {
        return DefaultTableMeta.getChildTableMeta(domainClass);
    }

    public static <S extends IDomain, T extends S> ChildTableMeta<T> getChildTableMeta(
            ParentTableMeta<S> parentTableMeta, Class<T> domainClass) {
        return DefaultTableMeta.getChildTableMeta(parentTableMeta, domainClass);
    }

    public static Map<Class<?>, TableMeta<?>> getTableMetaMap(final SchemaMeta schemaMeta
            , final List<String> basePackages) throws TableMetaLoadException {
        return Collections.emptyMap();
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
