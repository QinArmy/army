package io.army.dialect;

import io.army.bean.ReadWrapper;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.time.temporal.Temporal;

/**
 * <p>
 * package interface
*
 * @since 1.0
 */
interface RowWrapper {

    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     *
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    void set(FieldMeta<?> field, @Nullable Object value);

    /**
     * <p>
     * This method is invoked by {@link  FieldValueGenerator#validate(TableMeta, RowWrapper) }.
     * Note: couldn't read value from default expression map.
     *
     *
     * @see FieldValueGenerator#validate(TableMeta, RowWrapper)
     */
    boolean isNullValueParam(FieldMeta<?> field);


    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     *
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    ReadWrapper readonlyWrapper();

    /**
     * @return context create time for createTime field
     */
    Temporal getCreateTime();

    boolean isManageVisible();


}
