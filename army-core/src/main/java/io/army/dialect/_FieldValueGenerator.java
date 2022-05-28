package io.army.dialect;

import io.army.bean.ObjectAccessor;
import io.army.bean.ReadWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface _FieldValueGenerator {

    void generate(TableMeta<?> table, IDomain domain, ObjectAccessor accessor, ReadWrapper readWrapper);

    void validate(TableMeta<?> table, IDomain domain, ObjectAccessor accessor);
}
