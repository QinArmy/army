package io.army.beans;

interface TripeWrapper extends BeanWrapper {

    /**
     * @return create new {@code Triple} every invoke.
     */
    @Override
    Object getWrappedInstance() throws BeansException;
}
