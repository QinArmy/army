package io.army.beans;

interface PairWrapper extends ObjectWrapper {

    /**
     * @return create new {@link io.army.util.Pair} every invoke.
     */
    @Override
    Object getWrappedInstance();
}
