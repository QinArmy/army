package io.army.criteria;

import io.army.dialect.DML;
import io.army.dialect.DQL;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQLWrapper;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.List;

public interface SQLContext {

    /**
     * <p>
     * the key of sharding  {@link TableMeta} in same database.
     * </p>
     *
     * @param tableMeta {@link TableMeta} that will be append table name .
     */
    default void appendTable(TableMeta<?> tableMeta) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * the key of sharding  {@link ParentTableMeta} in same database.
     * </p>
     *
     * @param childTableMeta {@link io.army.meta.ChildTableMeta}'s {@link io.army.meta.ParentTableMeta}
     *                       that will be append table name .
     */
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

    StringBuilder sqlBuilder();

    void appendParam(ParamWrapper paramWrapper);

    List<ParamWrapper> paramList();

    default SQLWrapper build() {
        throw new UnsupportedOperationException();
    }


}
