package io.army.meta;

import io.army.dialect.Dialect;
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

    FieldMeta<? super T, ?> primaryKey();

    MappingMode mappingMode();

    List<FieldMeta<T, ?>> indexPropList();

    List<FieldMeta<T, ?>> uniquePropList();

    List<FieldMeta<T, ?>> fieldList();

    String createSql(Dialect dialect);

    String charset();

    String schema();

    boolean primaryDesc();

    <F> FieldMeta<T, F> getField(String propName, Class<F> propClass) throws MediaException;
}
