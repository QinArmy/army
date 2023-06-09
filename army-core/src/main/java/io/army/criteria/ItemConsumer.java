package io.army.criteria;

public interface ItemConsumer<T> {


    ItemConsumer<T> accept(T item);


}
