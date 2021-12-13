package io.army.dialect.mysql;

import io.army.meta.FieldMeta;

class MySQL80DDL extends MySQL57DDL {

    public MySQL80DDL(MySQL80Dialect mysql) {
        super(mysql);
    }


    @Override
    protected final void doDefaultExpression(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
        String defaultExp = fieldMeta.defaultValue();
        if (defaultExp.startsWith("(") && defaultExp.endsWith(")")) {
            builder.append(defaultExp);
        } else {
            super.doDefaultExpression(fieldMeta, builder);
        }

    }
}
