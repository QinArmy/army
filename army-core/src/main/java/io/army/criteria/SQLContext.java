package io.army.criteria;

import io.army.dialect.DML;
import io.army.dialect.DQL;
import io.army.dialect.ParamWrapper;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.List;

public interface SQLContext {

    default void appendTable(TableMeta<?> tableMeta) {
        throw new UnsupportedOperationException();
    }

    default void appendParentTableOf(ChildTableMeta<?> childTableMeta) {
        throw new UnsupportedOperationException();
    }

    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException;

    default void appendField(FieldMeta<?, ?> fieldMeta) {

    }

    void appendText(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendTextValue(MappingType mappingType, Object value);

    DML dml();

    default DQL dql() {
        throw new UnsupportedOperationException();
    }

    StringBuilder stringBuilder();

    void appendParam(ParamWrapper paramWrapper);

    List<ParamWrapper> paramWrapper();


}
