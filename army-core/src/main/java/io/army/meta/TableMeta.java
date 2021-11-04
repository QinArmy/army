package io.army.meta;

import io.army.criteria.TableAble;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.sharding.Route;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @see SchemaMeta
 * @see FieldMeta
 * @see IndexMeta
 * @see IndexFieldMeta
 */
public interface TableMeta<T extends IDomain> extends TableAble, Meta {

    String ID = "id";

    String CREATE_TIME = "createTime";

    Set<String> DOMAIN_PROPS = ArrayUtils.asUnmodifiableSet(ID, CREATE_TIME);

    String UPDATE_TIME = "updateTime";


    String VISIBLE = "visible";

    String VERSION = "version";

    Set<String> UPDATE_PROPS = ArrayUtils.asUnmodifiableSet(
            DOMAIN_PROPS, UPDATE_TIME);

    Set<String> RESERVED_PROPS = ArrayUtils.asUnmodifiableSet(ID, CREATE_TIME, UPDATE_TIME, VISIBLE, VERSION);

    Class<T> javaType();

    String tableName();

    boolean immutable();

    String comment();

    PrimaryFieldMeta<? super T, Object> id();

    <F> PrimaryFieldMeta<T, F> id(Class<F> propClass) throws MetaException;

    MappingMode mappingMode();

    int discriminatorValue();

    boolean sharding();

    /**
     * @param database true : database route field list,false : table route field list.
     * @return a unmodifiable list
     */
    List<FieldMeta<?, ?>> routeFieldList(boolean database);

    @Nullable
    Class<? extends Route> routeClass();

    @Nullable
    ParentTableMeta<? super T> parentMeta();

    @Nullable
    <E extends Enum<E> & CodeEnum> FieldMeta<? super T, E> discriminator();

    /**
     * contain primary key
     */
    Collection<IndexMeta<T>> indexCollection();

    Collection<FieldMeta<T, ?>> fieldCollection();

    String charset();

    SchemaMeta schema();

    boolean mappingProp(String propName);

    FieldMeta<T, Object> getField(String propName) throws MetaException;

    <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) throws MetaException;

    <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MetaException;

    <F> UniqueFieldMeta<T, F> getUniqueField(String propName, Class<F> propClass) throws MetaException;

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
