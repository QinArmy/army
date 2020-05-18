package io.army.criteria;

import io.army.dialect.DQL;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.ParamWrapper;

public interface SQLContext {

    /**
     * <p>
     * the key of sharding  {@link TableMeta} in same database.
     * </p>
     *
     * @param tableMeta {@link TableMeta} that will be append table name .
     */
    void appendTable(TableMeta<?> tableMeta);

    /**
     * <p>
     * the key of sharding  {@link ParentTableMeta} in same database.
     * </p>
     *
     * @param childTableMeta {@link io.army.meta.ChildTableMeta}'s {@link io.army.meta.ParentTableMeta}
     *                       that will be append table name .
     */
    void appendParentTableOf(ChildTableMeta<?> childTableMeta);

    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException;

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendPredicate(SpecialPredicate predicate);

    void appendFieldPair(FieldPairDualPredicate predicate);

    void appendText(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendTextValue(MappingMeta mappingType, Object value);

    DQL dql();

    StringBuilder sqlBuilder();

    void appendParam(ParamWrapper paramWrapper);


}
