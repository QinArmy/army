package io.army.boot.migratioin;

import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;
import io.army.sqltype.MySQLDataType;

class MySQL80MetaSchemaComparator extends MySQL57MetaSchemaComparator {

    MySQL80MetaSchemaComparator(DialectSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Database database() {
        return Database.MySQL;
    }

    @Override
    protected boolean needModifyDefault(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo) throws SchemaInfoException, MetaException {
//        MySQLDataType mysqlType = (MySQLDataType) fieldMeta.mappingMeta().sqlDataType(database());
//        //TODO zoro support MYSQL57_NO_DEFAULT_TYPE_SET eLiteral value
//        boolean need;
//        if (TIME_TYPE_SET.contains(mysqlType)) {
//            String defaultValue = obtainDefaultValue(fieldMeta);
//            if (defaultExpression(defaultValue)) {
//                need = needModifyTimeTypeDefault(defaultValue, mysqlType, fieldMeta, columnInfo);
//            } else {
//                need = super.needModifyDefault(fieldMeta, columnInfo);
//            }
//        } else {
//            need = super.needModifyDefault(fieldMeta, columnInfo);
//        }
//        return need;
        return false;
    }


    private boolean needModifyTimeTypeDefault(String defaultExp, MySQLDataType mysqlType
            , FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo) {
        final String defaultValue = defaultExp.substring(1, defaultExp.length() - 2);

        switch (mysqlType) {
            case TIME:
            case DATE:
            case DATETIME:
            case YEAR:
                break;
            default:
                throw new IllegalStateException(String.format(
                        "%s couldn't recognize time type[%s]", getClass().getName(), mysqlType));
        }
        return false;
    }


    private boolean defaultExpression(String defaultValue) {
        return defaultValue.length() > 2 && defaultValue.startsWith("(") && defaultValue.endsWith(")");
    }
}
