package io.army.criteria;


public interface RowConstructor<F extends TableField> extends ColumnConsumer<F> {

    /**
     * @return this
     */
    ColumnConsumer<F> row();

}
