package io.army.dialect;

import io.army.bean.ReadWrapper;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

interface RowWrapper {

    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     * </p>
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    void set(FieldMeta<?> field, Object value);

    /**
     * <p>
     * This method is invoked by {@link  FieldValueGenerator#validate(TableMeta, RowWrapper) }.
     * Note: couldn't read value from default expression map.
     * </p>
     *
     * @see FieldValueGenerator#validate(TableMeta, RowWrapper)
     */
    boolean isNullValueParam(FieldMeta<?> field);


    /**
     * <p>
     * This method is invoked by {@link FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)}
     * </p>
     *
     * @see FieldValueGenerator#generate(TableMeta, boolean, RowWrapper)
     */
    ReadWrapper readonlyWrapper();


}
