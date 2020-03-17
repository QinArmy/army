package io.army.criteria.impl;

import io.army.criteria.AliasFieldMeta;
import io.army.criteria.SQLContext;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

/**
 * created  on 2019-02-22.
 */
final class FieldSelection<E> extends AbstractExpression<E> {

    private final FieldMeta<?, E> fieldMeta;

    FieldSelection(FieldMeta<?, E> fieldMeta, String alias) {
        this.fieldMeta = fieldMeta;
        this.as(alias);
    }

    @Override
    public MappingType mappingType() {
        return fieldMeta.mappingType();
    }

    @Override
    protected void afterSpace(SQLContext context) {
        this.fieldMeta.appendSQL(context);
    }

    @Override
    public String beforeAs() {
        String tableAlias;
        if (fieldMeta instanceof AliasFieldMeta) {
            tableAlias = ((AliasFieldMeta<?, ?>) fieldMeta).tableAlias();
        } else {
            tableAlias = "";
        }
        return tableAlias + "." + fieldMeta.propertyName();
    }
}
