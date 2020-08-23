package io.army.beans;

interface PairWrapper extends ObjectWrapper {

    /**
     * @return create new {@code Pair} every invoke.
     */
    @Override
    Object getWrappedInstance() throws BeansException;
}
