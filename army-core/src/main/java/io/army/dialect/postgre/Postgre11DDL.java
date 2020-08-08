package io.army.dialect.postgre;

import io.army.criteria.MetaException;
import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.dialect.DDLUtils;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is a implementation of {@link io.army.dialect.DDL} for Postgre 11.x .
 */
class Postgre11DDL extends AbstractDDL {


    Postgre11DDL(Postgre11Dialect dialect) {
        super(dialect);
    }

    /*################################## blow DDL method ##################################*/

    @Override
    protected final void tableOptionsClause(DDLContext context) {
        //no-op
    }

    @Override
    protected final void doDefaultForCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        int precision = fieldMeta.precision();
        if (precision > 6) {
            throw new MetaException("%s,this value range of Prestgre time type is [0,6] .", fieldMeta);
        }
        StringBuilder builder = context.sqlBuilder();
        Class<?> javaType = fieldMeta.javaType();
        if (javaType == LocalDateTime.class) {
            builder.append("LOCALTIMESTAMP");
        } else if (javaType == ZonedDateTime.class || javaType == OffsetDateTime.class) {
            builder.append("CURRENT_TIMESTAMP");
        } else {
            throw DDLUtils.createPropertyNotSupportJavaTypeException(fieldMeta, database());
        }
        if (precision > 0) {
            builder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    @Override
    protected void doDefaultExpression(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {

    }


    @Override
    protected Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        return Postgre11DDLUtils.createJdbcFunctionMap();
    }

    @Override
    protected void independentIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {

    }

    @Override
    protected void independentTableComment(DDLContext context) {

    }

    @Override
    protected void independentColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {

    }

    @Override
    protected boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta) {
        return false;
    }


    @Override
    protected final boolean useIndependentIndexDefinition() {
        return true;
    }

    @Override
    protected final boolean useIndependentComment() {
        return true;
    }

    @Override
    protected final void doNowExpressionForDefaultClause(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {

    }
}
