package io.army.beans;

interface TripeWrapper extends ObjectWrapper {

    /**
     * @return create new {@link io.army.util.Triple} every invoke.
     */
    @Override
    Object getWrappedInstance();
}
