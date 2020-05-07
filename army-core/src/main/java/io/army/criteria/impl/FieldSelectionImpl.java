package io.army.criteria.impl;

import io.army.criteria.AliasField;
import io.army.criteria.FieldSelection;
import io.army.criteria.SQLContext;
import io.army.meta.FieldExp;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingMeta;

/**
 * created  on 2019-02-22.
 */
final class FieldSelectionImpl<E> extends AbstractExpression<E> implements FieldSelection {

    private final FieldExp<?, E> fieldExp;

    FieldSelectionImpl(FieldExp<?, E> fieldExp, String alias) {
        this.fieldExp = fieldExp;
        this.as(alias);
    }

    @Override
    public FieldMeta<?, ?> fieldMeta() {
        return fieldExp.fieldMeta();
    }

    @Override
    public MappingMeta mappingMeta() {
        return fieldExp.mappingMeta();
    }

    @Override
    protected void afterSpace(SQLContext context) {
        this.fieldExp.appendSQL(context);
    }

    @Override
    public String beforeAs() {
        String tableAlias;
        if (fieldExp instanceof AliasField) {
            tableAlias = ((AliasField<?, ?>) fieldExp).tableAlias();
        } else {
            tableAlias = "";
        }
        return tableAlias + "." + fieldExp.propertyName();
    }
}
