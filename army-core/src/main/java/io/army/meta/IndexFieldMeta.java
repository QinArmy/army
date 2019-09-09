package io.army.meta;


import io.army.domain.IDomain;

public interface IndexFieldMeta<T extends IDomain, F> extends FieldMeta<T, F> {

    IndexMeta<T> indexMeta();

    boolean fieldAsc();

}
