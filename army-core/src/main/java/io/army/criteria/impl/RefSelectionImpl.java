package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.dialect.Database;
import io.army.dialect.MappingContext;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect.SQL;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.meta.mapping.ResultColumnMeta;
import io.army.sqltype.SQLDataType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class RefSelectionImpl<E> extends AbstractExpression<E> implements RefSelection<E> {


    static <E> RefSelectionImpl<E> buildImmutable(String subQueryAlias, String derivedFieldName
            , MappingMeta mappingMeta) {
        return new ImmutableRefSelection<>(subQueryAlias, derivedFieldName, mappingMeta);
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
        SQL sql = context.dql();

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

        private final MappingMeta mappingMeta;


        private ImmutableRefSelection(String subQueryAlias, String derivedFieldName, MappingMeta mappingMeta) {
            super(subQueryAlias, derivedFieldName);
            this.mappingMeta = mappingMeta;
        }

        @Override
        public boolean finished() {
            return true;
        }

        @Override
        public MappingMeta mappingMeta() {
            return this.mappingMeta;
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
            return this.proxyMappingType.mappingMeta != null;
        }

        @Override
        public MappingMeta mappingMeta() {
            return proxyMappingType.mappingMeta == null
                    ? proxyMappingType
                    : proxyMappingType.mappingMeta;
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


    private static final class ProxyMappingType implements MappingMeta {

        private MappingMeta mappingMeta;

        private ProxyMappingType() {

        }

        private void mappingMeta(MappingMeta mappingMeta) {
            Assert.state(this.mappingMeta == null, "mappingMeta not null.");
            this.mappingMeta = mappingMeta;
        }

        @Override
        public Class<?> javaType() {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta.javaType();
        }

        @Override
        public JDBCType jdbcType() {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta.jdbcType();
        }


        @Override
        public void nonNullSet(PreparedStatement st, Object nonNullValue, int index, MappingContext context)
                throws SQLException {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            this.mappingMeta.nonNullSet(st, nonNullValue, index, context);
        }

        @Override
        public Object nullSafeGet(ResultSet resultSet, String alias, ResultColumnMeta resultColumnMeta
                , MappingContext context) throws SQLException {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta.nullSafeGet(resultSet, alias, resultColumnMeta, context);
        }

        @Override
        public String toConstant(@Nullable FieldMeta<?, ?> paramMeta, Object nonNullValue) {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta.toConstant(paramMeta, nonNullValue);
        }

        @Override
        public SQLDataType sqlDataType(Database database) throws NotSupportDialectException {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta.sqlDataType(database);
        }

        @Override
        public MappingMeta mappingMeta() {
            Assert.state(this.mappingMeta != null, "no mappingMeta.");
            return this.mappingMeta;
        }
    }

}
