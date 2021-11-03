package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect.SqlDialect;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.ResultColumnMeta;
import io.army.meta.FieldMeta;
import io.army.sqldatatype.SqlType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class RefSelectionImpl<E> extends AbstractExpression<E> implements RefSelection<E> {


    static <E> RefSelectionImpl<E> buildImmutable(String subQueryAlias, String derivedFieldName
            , MappingType mappingType) {
        return new ImmutableRefSelection<>(subQueryAlias, derivedFieldName, mappingType);
    }


    static <E> RefSelectionImpl<E> buildOnceChange(String subQueryAlias, String derivedFieldName) {
        return new OnceChangeRefSelection<>(subQueryAlias, derivedFieldName);
    }

    final String subQueryAlias;

    final String derivedFieldName;

    private RefSelectionImpl(String subQueryAlias, String derivedFieldName) {
        this.subQueryAlias = subQueryAlias;
        this.derivedFieldName = derivedFieldName;
    }

    @Override
    public final Selection as(String alias) {
        return new DefaultSelection(this, alias);
    }

    @Override
    public final void appendSQL(SQLContext context) {
        SqlDialect sql = context.dql();

        context.sqlBuilder()
                .append(" ")
                .append(sql.quoteIfNeed(this.subQueryAlias))
                .append(".")
                .append(sql.quoteIfNeed(this.derivedFieldName()));
    }

    @Override
    public final String subQueryAlias() {
        return this.subQueryAlias;
    }

    @Override
    public final String derivedFieldName() {
        return this.derivedFieldName;
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    @Override
    public final String toString() {
        return subQueryAlias + "." + derivedFieldName();
    }


    private static final class ImmutableRefSelection<E> extends RefSelectionImpl<E> {

        private final MappingType mappingType;


        private ImmutableRefSelection(String subQueryAlias, String derivedFieldName, MappingType mappingType) {
            super(subQueryAlias, derivedFieldName);
            this.mappingType = mappingType;
        }

        @Override
        public boolean finished() {
            return true;
        }

        @Override
        public MappingType mappingMeta() {
            return this.mappingType;
        }

        @Override
        public void selection(String subQueryAlias, Selection selection) {
            throw new UnsupportedOperationException(String.format("selection[%s] is immutable.", this));
        }

    }

    private static final class OnceChangeRefSelection<E> extends RefSelectionImpl<E> {

        private final ProxyMappingType proxyMappingType = new ProxyMappingType();

        private OnceChangeRefSelection(String subQueryAlias, String derivedFieldName) {
            super(subQueryAlias, derivedFieldName);
        }

        @Override
        public boolean finished() {
            return this.proxyMappingType.mappingType != null;
        }

        @Override
        public MappingType mappingMeta() {
            return proxyMappingType.mappingType == null
                    ? proxyMappingType
                    : proxyMappingType.mappingType;
        }

        @Override
        public void selection(String subQueryAlias, Selection selection) {
            if (this.subQueryAlias.equals(subQueryAlias) && this.derivedFieldName.equals(selection.alias())) {
                this.proxyMappingType.mappingMeta(selection.mappingMeta());
            } else {
                throw new IllegalArgumentException(String.format(
                        "this.subQueryAlias[%s] this.derivedFieldName[%s] and  subQueryAlias[%s] selection.alias[%s]"
                        , this.subQueryAlias, this.derivedFieldName, subQueryAlias, selection.alias()));
            }
        }

    }


    private static final class ProxyMappingType implements MappingType {

        private MappingType mappingType;

        private ProxyMappingType() {

        }

        private void mappingMeta(MappingType mappingType) {
            Assert.state(this.mappingType == null, "mappingMeta not null.");
            this.mappingType = mappingType;
        }

        @Override
        public Class<?> javaType() {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.javaType();
        }

        @Override
        public JDBCType jdbcType() {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.jdbcType();
        }


        @Override
        public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
                throws SQLException {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            this.mappingType.nonNullSet(st, nonNullValue, index, context);
        }

        @Override
        public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
                , MappingContext context) throws SQLException {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.nullSafeGet(resultSet, alias, resultColumnMeta, context);
        }

        @Override
        public String toConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.toConstant(paramMeta, nonNullValue);
        }

        @Override
        public SqlType sqlDataType(Database database) throws NotSupportDialectException {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.sqlDataType(database);
        }

        @Override
        public MappingType mappingMeta() {
            Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType;
        }
    }

}
