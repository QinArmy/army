package io.army.criteria.dialect;

import io.army.meta.Field;
import io.army.meta.SQLType;

/**
 * created  on 2019-02-22.
 */
public interface Dialect {


    String getDefinit(Field<?, ?> field);

    SQLType sqlType(Field<?, ?> field);

}
