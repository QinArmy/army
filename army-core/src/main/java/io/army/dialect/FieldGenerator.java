package io.army.dialect;

import io.army.bean.ObjectAccessor;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface FieldGenerator {

    void generate(TableMeta<?> table, IDomain domain, ObjectAccessor accessor, boolean migration);
}
