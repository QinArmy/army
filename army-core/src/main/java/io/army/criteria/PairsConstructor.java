package io.army.criteria;


public interface PairsConstructor<F extends TableField> extends PairConsumer<F> {

    /**
     * <p>
     * Start one new row.
     * </p>
     *
     * @return this
     */
    PairConsumer<F> row();

}
