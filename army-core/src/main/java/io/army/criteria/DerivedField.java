package io.army.criteria;

public interface DerivedField<E> extends Expression<E>, Selection {

    String tableName();

}
