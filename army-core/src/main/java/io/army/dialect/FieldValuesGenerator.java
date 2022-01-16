package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.meta.TableMeta;

public interface FieldValuesGenerator {

    void generate(TableMeta<?> table, ObjectWrapper wrapper, boolean migrationData);
}
