package io.army.criteria.impl;

final class ObjectHolder<T> {

    private T data;

    ObjectHolder() {
    }


    T get() {
        return data;
    }

    void set(T data) {
        this.data = data;
    }


}
