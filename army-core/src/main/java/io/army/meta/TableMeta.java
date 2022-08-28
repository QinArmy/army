package io.army.meta;

import io.army.criteria.TabularItem;
import io.army.lang.Nullable;

import java.util.List;

/**
 * @see SchemaMeta
 * @see FieldMeta
 * @see IndexMeta
 * @see IndexFieldMeta
 */
public interface TableMeta<T> extends TabularItem, DatabaseObject {


    Class<T> javaType();

    /**
     * <p>
     * Table name,Equivalence : {@link  FieldMeta#objectName()}
     * </p>
     */
    String tableName();

    boolean immutable();

    String comment();

    PrimaryFieldMeta<T> id();

    PrimaryFieldMeta<? super T> nonChildId();


    @Nullable
    FieldMeta<? super T> version();

    @Nullable
    FieldMeta<? super T> visible();

    @Nullable
    FieldMeta<? super T> discriminator();

    int discriminatorValue();


    /**
     * contain primary key
     */
    List<IndexMeta<T>> indexList();

    List<FieldMeta<T>> fieldList();

    String charset();

    SchemaMeta schema();

    boolean containField(String fieldName);

    boolean containComplexField(String fieldName);

    boolean isField(FieldMeta<?> field);

    boolean isComplexField(FieldMeta<?> field);

    /**
     * @throws IllegalArgumentException when not found matched {@link FieldMeta} for fieldName
     */
    FieldMeta<T> getField(String fieldName);

    @Nullable
    FieldMeta<T> tryGetField(String fieldName);


    FieldMeta<? super T> getComplexFiled(String filedName);

    @Nullable
    FieldMeta<? super T> tryGetComplexFiled(String filedName);

    /**
     * @throws IllegalArgumentException when not found matched {@link IndexFieldMeta} for fieldName
     */
    IndexFieldMeta<T> getIndexField(String fieldName);

    /**
     * @throws IllegalArgumentException when not found matched {@link UniqueFieldMeta} for fieldName
     */
    UniqueFieldMeta<T> getUniqueField(String fieldName);

    List<FieldMeta<?>> fieldChain();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
