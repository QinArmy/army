package io.army.dialect;

import io.army.bean.ObjectWrapper;
import io.army.meta.TableMeta;

public interface FieldValuesGenerator {

    void generate(TableMeta<?> table, ObjectWrapper wrapper, boolean migrationData);
}
