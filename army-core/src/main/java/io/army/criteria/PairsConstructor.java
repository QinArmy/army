package io.army.criteria;


public interface PairsConstructor<T> extends PairConsumer<T> {

    /**
     * <p>
     * Start one new row.
     * </p>
     *
     * @return this
     */
    PairConsumer<T> row();

}
