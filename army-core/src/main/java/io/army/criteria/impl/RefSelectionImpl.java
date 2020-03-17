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

final class RefSelectionImpl<E> extends AbstractExpression<E> implements RefSelection<E> {

    private final String subQueryAlias;

    private final String derivedFieldName;

    private final ProxyMappingType proxyMappingType = new ProxyMappingType();

    RefSelectionImpl(String subQueryAlias, String derivedFieldName) {
        this.subQueryAlias = subQueryAlias;
        this.derivedFieldName = derivedFieldName;
    }

    RefSelectionImpl(String subQueryAlias, String derivedFieldName, Class<E> selectionType) {
        this(subQueryAlias,derivedFieldName);
    }

    @Override
    protected void afterSpace(SQLContext context) {

        SQL sql = context.dml();
        context.stringBuilder()
                .append(sql.quoteIfNeed(subQueryAlias))
                .append(".")
                .append(sql.quoteIfNeed(derivedFieldName))
               ;
    }

    @Override
    public MappingType mappingType() {
        return this.proxyMappingType;
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
    public final Selection selection() {
        return this.proxyMappingType.selection();
    }

    @Override
    public void selection(Selection selection) {
        Assert.isTrue(this.derivedFieldName.equals(selection.alias()),()->String.format
                ("selection[%s] alias and %s.%s not match.",selection,this.subQueryAlias,this.derivedFieldName));
        this.proxyMappingType.selection(selection);
    }

    @Override
    protected String beforeAs() {
        return subQueryAlias + "." + derivedFieldName;
    }



    private static final class ProxyMappingType  implements MappingType {

        private Selection selection;

        private ProxyMappingType() {

        }

        private void selection(Selection selection){
            Assert.state(this.selection == null,"selection only update once.");
            this.selection = selection;
        }

        private Selection selection(){
            Assert.state(this.selection != null,"no selection.");
            return selection;
        }

        @Override
        public Class<?> javaType() {
            Assert.state(this.selection != null,"no selection.");
            return selection.mappingType().javaType();
        }

        @Override
        public JDBCType jdbcType() {
            Assert.state(this.selection != null,"no selection.");
            return selection.mappingType().jdbcType();
        }

        @Override
        public boolean isTextValue(String textValue) {
            Assert.state(this.selection != null,"no selection.");
            return selection.mappingType().isTextValue(textValue);
        }

        @Override
        public void nonNullSet(PreparedStatement st, Object value, int index) throws SQLException {
            Assert.state(this.selection != null,"no selection.");
            selection.mappingType().nonNullSet(st,value,index);
        }

        @Override
        public Object nullSafeGet(ResultSet resultSet, String alias) throws SQLException {
            Assert.state(this.selection != null,"no selection.");
            return selection.mappingType().nullSafeGet(resultSet,alias);
        }

        @Override
        public String nonNullTextValue(Object value) {
            Assert.state(this.selection != null,"no selection.");
            return selection.mappingType().nonNullTextValue(value);
        }
    }

}
