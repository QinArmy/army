package io.army.dialect.mysql;

import io.army.criteria.MetaException;
import io.army.dialect.AbstractDDL;
import io.army.dialect.DDLContext;
import io.army.dialect.DDLUtils;
import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

class MySQL57DDL extends AbstractDDL {


    MySQL57DDL(MySQL57Dialect mysql) {
        super(mysql);
        Assert.notNull(mysql, "mysql required");
    }

    /*################################## blow SQL interface method ##################################*/

    /*################################## blow DDL method ##################################*/


    /*################################## blow AbstractTableDDL template method ##################################*/

    @Override
    protected final void doDefaultForCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        if (fieldMeta.javaType() != LocalDateTime.class) {
            throw DDLUtils.createPropertyNotSupportJavaTypeException(fieldMeta, database());
        }
        StringBuilder builder = context.sqlBuilder();
        int precision = fieldMeta.precision();
        if (precision > 6) {
            throw new MetaException("%s,this value range of MySQL DATETIME is [0,6] .", fieldMeta);
        }
        builder.append("CURRENT_TIMESTAMP");
        if (precision > 0) {
            builder.append("(")
                    .append(precision)
                    .append(")");
        }
    }

    @Override
    protected Map<JDBCType, Function<FieldMeta<?, ?>, String>> createJdbcFunctionMap() {
        return MySQL57DDLUtils.createJdbcFunctionMap();
    }

    @Override
    protected void tableOptionsClause(DDLContext context) {
        TableMeta<?> tableMeta = context.tableMeta();
        context.sqlBuilder()
                .append(" ENGINE = InnoDB CHARACTER SET = ")
                .append(MySQL57DDLUtils.tableCharset(tableMeta.charset()))
                .append(" COMMENT '")
                .append(tableMeta.comment())
                .append("'")
        ;
    }

    @Override
    protected void doDefaultExpression(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
        builder.append(
                MySQL57DDLUtils.quoteDefaultIfNeed(fieldMeta, escapeQuote(fieldMeta.defaultValue()))
        );
    }


    @Override
    protected final boolean hasDefaultClause(FieldMeta<?, ?> fieldMeta) {
        return MySQL57DDLUtils.NO_DEFAULT_JDBC.contains(fieldMeta.mappingMeta().jdbcType());
    }

    @Override
    protected final boolean useIndependentIndexDefinition() {
        return false;
    }

    @Override
    protected final boolean useIndependentComment() {
        return false;
    }

    @Override
    protected final void independentIndexDefinitionClause(IndexMeta<?> indexMeta, DDLContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void independentTableComment(DDLContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected final void independentColumnComment(FieldMeta<?, ?> fieldMeta, DDLContext context) {
        throw new UnsupportedOperationException();
    }

    /*################################## blow protected method ##################################*/


    protected void doNowExpressionForDefaultClause(FieldMeta<?, ?> fieldMeta, StringBuilder builder) {
        if (fieldMeta.javaType() != LocalDateTime.class) {
            throw DDLUtils.createNowExpressionNotSupportJavaTypeException(fieldMeta, database());
        }
        MySQL57DDLUtils.nowFunc(fieldMeta, builder);
    }



    /*################################## blow private method ##################################*/


}
