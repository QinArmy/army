package io.army.criteria.dialect;

import io.army.meta.Field;

/**
 * created  on 2019-02-22.
 */
public interface Dialect {


    String fielddefinition(Field<?, ?> field);


}
