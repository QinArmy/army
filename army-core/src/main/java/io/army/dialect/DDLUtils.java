package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.util.Assert;

import java.sql.JDBCType;

public abstract class DDLUtils {


    public static void assertJDBC(FieldMeta<?, ?> fieldMeta, JDBCType jdbcType) {
        if (fieldMeta.mappingType().jdbcType() != jdbcType) {
            throw new IllegalArgumentException(String.format("Entity[%s].column[%s] mapping jdbc error",
                    fieldMeta.table().tableName(),
                    fieldMeta.fieldName()
            )
            );
        }
    }


    public static int getNumberPrecision(FieldMeta<?, ?> fieldMeta, int minPrecision, int maxPrecision) {
        int precision = fieldMeta.precision();
        if (precision < minPrecision) {
            precision = fieldMeta.mappingType().precision();
        }
        Assert.isTrue(precision <= maxPrecision, () -> String.format("Entity[%s].column[%s] precision must in [%s,%s]",
                fieldMeta.table().tableName(),
                fieldMeta.fieldName(),
                minPrecision,
                maxPrecision
                )
        );
        return precision;
    }

    public static int getNumberScale(FieldMeta<?, ?> fieldMeta, int minScale, int maxScale) {
        int scale = fieldMeta.scale();
        if (scale < minScale) {
            scale = fieldMeta.mappingType().scale();
        }
        Assert.isTrue(scale <= maxScale, () -> String.format("Entity[%s].column[%s] precision must in [1,%s]",
                fieldMeta.table().tableName(),
                fieldMeta.fieldName(),
                maxScale
                )
        );
        return maxScale;
    }
}
