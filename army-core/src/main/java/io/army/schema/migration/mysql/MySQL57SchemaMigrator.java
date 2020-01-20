package io.army.schema.migration.mysql;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.dialect.DialectUtils;
import io.army.meta.FieldMeta;
import io.army.schema.SchemaInfoException;
import io.army.schema.migration.AbstractSchemaMigrator;
import io.army.schema.migration.ColumnInfo;
import io.army.sqltype.MySQLDataType;

public class MySQL57SchemaMigrator extends AbstractSchemaMigrator implements MySQLSchemaMigrator {

    @Override
    protected boolean precisionOrScaleAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        MySQLDataType sqlDataType;
        try {
            sqlDataType = MySQLDataType.valueOf(columnInfo.sqlType());
        } catch (IllegalArgumentException e) {
            throw new SchemaInfoException(ErrorCode.NNSUPPORT_SQL_TYPE,
                    "table[%s].column[%s]'s sql type[%s] isn't supported",
                    columnInfo.table().name(),
                    columnInfo.name(),
                    columnInfo.sqlType());
        }

        try {
            return !sqlDataType.precisionMatch(fieldMeta.precision(), columnInfo.precision())
                    || !sqlDataType.scaleMatch(fieldMeta.precision(), columnInfo.scale());
        } catch (IllegalArgumentException e) {
            throw new MetaException(ErrorCode.NNSUPPORT_SQL_TYPE,
                    e,
                    "Entity[%s].prop[%s] definition error,%s",
                    fieldMeta.table().javaType(),
                    fieldMeta.propertyName(),
                    e.getMessage()
            );
        }
    }

    @Override
    protected boolean defaultValueAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        //TODO zoro implement interpreter model
        return !fieldMeta.isPrimary()
                && !fieldMeta.defaultValue().equals(columnInfo.defaultValue());
    }


}
