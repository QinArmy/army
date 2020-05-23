package io.army.beans;

interface PairWrapper extends BeanWrapper {

    /**
     * @return create new {@code Pair} every invoke.
     */
    @Override
    Object getWrappedInstance() throws BeansException;
}
