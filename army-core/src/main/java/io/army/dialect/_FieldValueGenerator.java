package io.army.dialect;

import io.army.bean.ObjectWrapper;
import io.army.meta.TableMeta;

public interface _FieldValueGenerator {

    void generate(TableMeta<?> table, ObjectWrapper wrapper);

    void validate(TableMeta<?> table, ObjectWrapper wrapper);
}
