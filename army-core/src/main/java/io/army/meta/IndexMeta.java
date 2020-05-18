package io.army.meta;

import io.army.domain.IDomain;

import java.util.List;

public interface IndexMeta<T extends IDomain> extends Meta {

    TableMeta<T> table();

    String name();

    List<IndexFieldMeta<T, ?>> fieldList();

    boolean isPrimaryKey();

    boolean unique();

    String type();

}
