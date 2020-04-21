package io.army.criteria.impl;

import io.army.criteria.SQLContext;
import io.army.criteria.Selection;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

abstract class RefSelectionImpl<E> extends AbstractExpression<E> implements RefSelection<E> {


    static <E> RefSelectionImpl<E> buildImmutable(String subQueryAlias, Selection selection) {
        return new ImmutableRefSelection<>(subQueryAlias, selection);
    }

    static <E> RefSelectionImpl<E> buildOnceChange(String subQueryAlias, String derivedFieldName) {
        return new OnceChangeRefSelection<>(subQueryAlias, derivedFieldName);
    }


    protected final String subQueryAlias;

    private RefSelectionImpl(String subQueryAlias) {
        this.subQueryAlias = subQueryAlias;
    }

    @Override
    public final String alias() {
        if (this.alias == null) {
            return derivedFieldName();
        } else {
            return this.alias;
        }
    }

    @Override
    protected final void afterSpace(SQLContext context) {
        SQL sql = context.dml();

        context.sqlBuilder()
                .append(sql.quoteIfNeed(subQueryAlias))
                .append(".")
                .append(sql.quoteIfNeed(derivedFieldName()));
    }

    @Override
    public final String subQueryAlias() {
        return this.subQueryAlias;
    }

    @Override
    protected final String beforeAs() {
        return subQueryAlias + "." + derivedFieldName();
    }


    private static final class ImmutableRefSelection<E> extends RefSelectionImpl<E> {

        private final Selection selection;

        private ImmutableRefSelection(String subQueryAlias, Selection selection) {
            super(subQueryAlias);
            this.selection = selection;
        }

        @Override
        public Selection selection() {
            return this.selection;
        }

        @Override
        public MappingType mappingType() {
            return this.selection.mappingType();
        }

        @Override
        public String derivedFieldName() {
            return this.selection.alias();
        }

        @Override
        public void selection(Selection selection) {
            throw new UnsupportedOperationException(String.format("selection[%s] is immutable.", this));
        }
    }

    private static final class OnceChangeRefSelection<E> extends RefSelectionImpl<E> {

        private final String derivedFieldName;

        private final ProxyMappingType proxyMappingType = new ProxyMappingType();

        private OnceChangeRefSelection(String subQueryAlias, String derivedFieldName) {
            super(subQueryAlias);
            this.derivedFieldName = derivedFieldName;
        }

        @Override
        public Selection selection() {
            return proxyMappingType.selection();
        }

        @Override
        public MappingType mappingType() {
            return proxyMappingType;
        }

        @Override
        public String derivedFieldName() {
            return this.derivedFieldName;
        }

        @Override
        public void selection(Selection selection) {
            Assert.isTrue(this.derivedFieldName.equals(selection.alias()), () -> String.format
                    ("selection[%s] alias and %s.%s not match.", selection, this.subQueryAlias, this.derivedFieldName));
            this.proxyMappingType.selection(selection);
        }
    }


    private static final class ProxyMappingType implements MappingType {

        private Selection selection;

        private ProxyMappingType() {

        }

        private void selection(Selection selection) {
            Assert.state(this.selection == null, "selection only singleUpdate once.");
            this.selection = selection;
        }

        private Selection selection() {
            Assert.state(this.selection != null, "no selection.");
            return selection;
        }

        @Override
        public Class<?> javaType() {
            Assert.state(this.selection != null, "no selection.");
            return selection.mappingType().javaType();
        }

        @Override
        public JDBCType jdbcType() {
            Assert.state(this.selection != null, "no selection.");
            return selection.mappingType().jdbcType();
        }

        @Override
        public boolean isTextValue(String textValue) {
            Assert.state(this.selection != null, "no selection.");
            return selection.mappingType().isTextValue(textValue);
        }

        @Override
        public void nonNullSet(PreparedStatement st, Object nonNullValue, int index) throws SQLException {
            Assert.state(this.selection != null, "no selection.");
            selection.mappingType().nonNullSet(st, nonNullValue, index);
        }

        @Override
        public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
            Assert.state(this.selection != null, "no selection.");
            return selection.mappingType().nullSafeGet(resultSet, alias);
        }

        @Override
        public String nonNullTextValue(Object value) {
            Assert.state(this.selection != null, "no selection.");
            return selection.mappingType().nonNullTextValue(value);
        }
    }

}
