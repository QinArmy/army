package io.army.meta;

import io.army.domain.IDomain;
import io.army.struct.CodeEnum;
import io.army.util.ArrayUtils;
import javafx.scene.media.MediaException;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

/**
 * created  on 2018/10/8.
 */
public interface TableMeta<T extends IDomain> {

    String ID = "id";

    String CREATE_TIME = "createTime";

    String VISIBLE = "visible";

    Set<String> DOMAIN_PROPS = ArrayUtils.asUnmodifiableSet(ID, CREATE_TIME, VISIBLE);

    String UPDATE_TIME = "updateTime";

    String VERSION = "version";

    Set<String> VERSION_PROPS = ArrayUtils.asUnmodifiableSet(
            DOMAIN_PROPS, UPDATE_TIME, VERSION);

    Class<T> javaType();

    String tableName();

    boolean immutable();

    String comment();

    @Nullable
    TableMeta<? super T> parent();

    IndexFieldMeta<? super T, ?> primaryKey();

    MappingMode mappingMode();

    <E extends Enum<E> & CodeEnum> FieldMeta<T, E> discriminator();

    int discriminatorValue();

    /**
     * contain primary key
     */
    Collection<IndexMeta<T>> indexCollection();

    Collection<FieldMeta<T, ?>> fieldCollection();

    String charset();

    String schema();

    <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) throws MediaException;

    <F> IndexFieldMeta<T, F> getIndexField(String propName, Class<F> propClass) throws MediaException;
}
