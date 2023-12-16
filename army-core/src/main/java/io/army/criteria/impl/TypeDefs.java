package io.army.criteria.impl;

import io.army.sqltype.DataType;

public abstract class TypeDefs {

    final DataType dataType;

    private TypeDefs(DataType dataType) {
        this.dataType = dataType;
    }


}
