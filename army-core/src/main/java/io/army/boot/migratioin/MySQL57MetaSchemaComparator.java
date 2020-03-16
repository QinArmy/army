package io.army.boot.migratioin;

import io.army.ErrorCode;
import io.army.criteria.MetaException;
import io.army.meta.FieldMeta;
import io.army.schema.SchemaInfoException;
import io.army.sqltype.MySQLDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MySQL57MetaSchemaComparator extends AbstractMetaSchemaComparator {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    MySQL57MetaSchemaComparator() {
    }

    @Override
    protected boolean precisionOrScaleAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        MySQLDataType sqlDataType;
        try {
            sqlDataType = MySQLDataType.valueOf(columnInfo.sqlType());
        } catch (IllegalArgumentException e) {
            throw new SchemaInfoException(ErrorCode.NNSUPPORT_SQL_TYPE,
                    "tableMeta[%s].column[%s]'s dml type[%s] isn'field supported",
                    columnInfo.table().name(),
                    columnInfo.name(),
                    columnInfo.sqlType());
        }

        try {

            boolean alter = !sqlDataType.precisionMatch(fieldMeta.precision(), columnInfo.columnSize())
                    || !sqlDataType.scaleMatch(fieldMeta.scale(), columnInfo.scale());
            if (alter) {
                LOG.debug("columnInfo:{}", columnInfo);
            }
            return alter;
        } catch (IllegalArgumentException e) {
            throw new MetaException(ErrorCode.NNSUPPORT_SQL_TYPE,
                    e,
                    "Entity[%s].prop[%s] definition error,%s",
                    fieldMeta.tableMeta().javaType(),
                    fieldMeta.propertyName(),
                    e.getMessage()
            );
        }
    }

    @Override
    protected boolean defaultValueAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        //TODO zoro implement interpreter model
        return false;
    }


}
