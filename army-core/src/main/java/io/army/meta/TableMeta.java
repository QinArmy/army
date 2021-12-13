package io.army.meta;

import io.army.criteria.TableAble;
import io.army.criteria._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.sharding.Route;
import io.army.struct.CodeEnum;

import java.util.Collection;
import java.util.List;

/**
 * @see SchemaMeta
 * @see FieldMeta
 * @see IndexMeta
 * @see IndexFieldMeta
 */
public interface TableMeta<T extends IDomain> extends TableAble, Meta {


    /**
     * @throws UnsupportedOperationException throw always
     */
    @Override
    void appendSql(_SqlContext context);


    Class<T> javaType();

    String tableName();

    boolean immutable();

    String comment();

    PrimaryFieldMeta<T, Object> id();

    <F> PrimaryFieldMeta<T, F> id(Class<F> idClass) throws MetaException;

    @Nullable
    <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator();

    int discriminatorValue();


    boolean sharding();

    /**
     * @param database true : database route field list,false : table route field list.
     * @return a unmodifiable list
     */
    @Deprecated
    List<FieldMeta<?, ?>> routeFieldList(boolean database);

    List<FieldMeta<?, ?>> databaseRouteFields();

    List<FieldMeta<?, ?>> tableRouteFields();

    @Nullable
    Class<? extends Route> routeClass();


    /**
     * contain primary key
     */
    Collection<IndexMeta<T>> indexCollection();

    Collection<FieldMeta<T, ?>> fieldCollection();

    String charset();

    SchemaMeta schema();

    boolean containField(String fieldName);

    /**
     * @throws IllegalArgumentException when not found matched {@link FieldMeta} for fieldName
     */
    FieldMeta<T, Object> getField(String fieldName);

    /**
     * @throws IllegalArgumentException when not found matched {@link FieldMeta} for fieldName
     */
    <F> FieldMeta<T, F> getField(String fieldName, Class<F> fieldClass);

    /**
     * @throws IllegalArgumentException when not found matched {@link IndexFieldMeta} for fieldName
     */
    <F> IndexFieldMeta<T, F> getIndexField(String fieldName, Class<F> fieldClass);

    /**
     * @throws IllegalArgumentException when not found matched {@link UniqueFieldMeta} for fieldName
     */
    <F> UniqueFieldMeta<T, F> getUniqueField(String fieldName, Class<F> fieldClass);

    List<FieldMeta<T, ?>> generatorChain();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    @Override
    String toString();


}
