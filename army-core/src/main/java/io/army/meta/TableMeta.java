package io.army.meta;

import io.army.domain.IDomain;
import javafx.scene.media.MediaException;

import java.util.List;

/**
 * created  on 2018/10/8.
 */
public interface TableMeta<T extends IDomain> {

    Class<T> javaType();

    String tableName();

    boolean immutable();

    int fieldCount();

    String comment();

    List<TableMeta<? super T>> parentList();

    <S extends T> List<TableMeta<? super S>> tableList(Class<S> sunClass);

    IndexFieldMeta<? super T, ?> primaryKey();

    MappingMode mappingMode();

    int discriminatorValue();

    /**
     * contain primary key
     */
    List<IndexMeta<T>> indexList();

    List<FieldMeta<T, ?>> fieldList();

    String charset();

    String schema();

    <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) throws MediaException;
}
