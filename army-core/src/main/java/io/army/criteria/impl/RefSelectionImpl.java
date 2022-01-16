package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.dialect.NotSupportDialectException;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.ParamMeta;
import io.army.meta.ServerMeta;
import io.army.sqltype.SqlType;
import io.army.util._Assert;

import java.sql.JDBCType;

abstract class RefSelectionImpl<E> extends OperationExpression<E> implements RefSelection<E> {


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
    public final void appendSql(_SqlContext context) {
//        SqlDialect sql = context.dialect();
//
//        context.sqlBuilder()
//                .append(" ")
//                .append(sql.quoteIfNeed(this.subQueryAlias))
//                .append(".")
//                .append(sql.quoteIfNeed(this.derivedFieldName()));
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
        public ParamMeta paramMeta() {
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
        public ParamMeta paramMeta() {
            return proxyMappingType.mappingType == null
                    ? proxyMappingType
                    : proxyMappingType.mappingType;
        }

        @Override
        public void selection(String subQueryAlias, Selection selection) {
            if (this.subQueryAlias.equals(subQueryAlias) && this.derivedFieldName.equals(selection.alias())) {
                this.proxyMappingType.mappingMeta(selection.paramMeta().mappingType());
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
            _Assert.state(this.mappingType == null, "mappingMeta not null.");
            this.mappingType = mappingType;
        }

        @Override
        public Class<?> javaType() {
            _Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.javaType();
        }

        @Override
        public JDBCType jdbcType() {
            _Assert.state(this.mappingType != null, "no mappingMeta.");
            return this.mappingType.jdbcType();
        }


        @Override
        public SqlType sqlType(ServerMeta serverMeta) throws NotSupportDialectException {
            return null;
        }

        @Override
        public Object convertBeforeBind(SqlType sqlDataType, Object nonNull) {
            return null;
        }

        @Override
        public Object convertAfterGet(SqlType sqlDataType, Object nonNull) {
            return null;
        }

        @Override
        public MappingType mappingType() {
            return null;
        }
    }

}
