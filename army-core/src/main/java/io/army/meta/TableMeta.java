package io.army.meta;

import io.army.criteria.MetaException;
import io.army.criteria.TableAble;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;

import java.util.Collection;
import java.util.Set;

/**
 * created  on 2018/10/8.
 *
 * @see SchemaMeta
 * @see FieldMeta
 * @see IndexMeta
 * @see IndexFieldMeta
 */
public interface TableMeta<T extends IDomain> extends TableAble, Meta {

    String ID = "id";

    String CREATE_TIME = "createTime";

    String VISIBLE = "visible";

    Set<String> DOMAIN_PROPS = ArrayUtils.asUnmodifiableSet(ID, CREATE_TIME);

    String UPDATE_TIME = "updateTime";

    String VERSION = "version";

    Set<String> VERSION_PROPS = ArrayUtils.asUnmodifiableSet(
            DOMAIN_PROPS, UPDATE_TIME, VERSION);

    Set<String> RESERVED_PROPS = ArrayUtils.asUnmodifiableSet(VERSION_PROPS, VISIBLE);

    Class<T> javaType();

    String tableName();

    boolean immutable();

    String comment();

    IndexFieldMeta<? super T, Object> id();

    MappingMode mappingMode();

    int discriminatorValue();

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


    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
