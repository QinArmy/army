package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.postgre.PostgreFuncColExp;
import io.army.dialect.TableDML;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.sqltype.PostgreSQLType;

/**
 * @param <E>
 * @see <a href="https://www.postgresql.org/docs/12/sql-select.html">Postgre from clause of select
 * about function column definition</a>
 */
final class PostgreFunColDefExpImpl<E> extends AbstractExpression<E> implements PostgreFuncColExp<E> {

    static <E> PostgreFuncColExp<E> build(String columnName, MappingType mappingType, @Nullable PostgreSQLType sqlType) {
        return new PostgreFunColDefExpImpl<>(columnName, mappingType, sqlType);
    }

    private final String columnName;

    private final MappingType mappingType;

    private final PostgreSQLType sqlType;


    private PostgreFunColDefExpImpl(String columnName, MappingType mappingType, @Nullable PostgreSQLType sqlType) {
        this.columnName = columnName;
        this.mappingType = mappingType;
        this.sqlType = sqlType;
    }

    @Override
    public String columnName() {
        return this.columnName;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        TableDML dml = context.dml();
        StringBuilder builder = context.stringBuilder()
                .append(dml.quoteIfNeed(this.columnName))
                .append(" ");
        if (sqlType == null) {
            builder.append(dml.mapping(this.mappingType));
        } else {
            builder.append(sqlType);
        }
    }

    @Override
    protected String beforeAs() {
        String text = columnName + " ";
        if (sqlType == null) {
            text += mappingType.jdbcType();
        } else {
            text += sqlType;
        }
        return text;
    }

    @Override
    public MappingType mappingType() {
        return mappingType;
    }

    /*################################## blow private method ##################################*/


}
