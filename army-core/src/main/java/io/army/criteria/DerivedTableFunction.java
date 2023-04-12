package io.army.criteria;

import io.army.criteria.standard.SQLFunction;
import io.army.meta.TypeMeta;

public interface DerivedTableFunction extends DerivedTable, SelectionSpec, SQLFunction, TypeInfer {


    @Override
    DerivedTableFunction mapTo(TypeMeta mapType);

}
