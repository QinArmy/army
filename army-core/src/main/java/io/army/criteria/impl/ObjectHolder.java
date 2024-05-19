package io.army.criteria.impl;

public final class ObjectHolder<T> {

    private T data;

    public ObjectHolder() {
    }


    public T get() {
        return data;
    }

    public void set(T data) {
        this.data = data;
    }


}
