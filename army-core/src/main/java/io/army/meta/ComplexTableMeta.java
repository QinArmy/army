package io.army.meta;


public interface ComplexTableMeta<P, T> extends ChildTableMeta<T> {

    @Override
    ParentTableMeta<P> parentMeta();

}
