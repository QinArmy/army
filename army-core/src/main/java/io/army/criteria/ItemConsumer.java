package io.army.criteria;

@Deprecated
public interface ItemConsumer<T> {


    ItemConsumer<T> accept(T item);


}
