package io.army.dialect.mysql;

import io.army.meta.FieldMeta;

class MySQL80TableDDL extends MySQL57TableDDL {

    public MySQL80TableDDL(My80Dialect mysql) {
        super(mysql);
    }


    @Override
    String handleDefaultValue(FieldMeta<?, ?> fieldMeta) {
        String defaultValue = fieldMeta.defaultValue().trim();
        if (MySQL80DDLUtils.isExpression(defaultValue)) {
            return defaultValue;
        }
        return super.handleDefaultValue(fieldMeta);
    }
}
