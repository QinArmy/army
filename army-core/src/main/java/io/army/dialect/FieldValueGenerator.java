package io.army.dialect;

import io.army.meta.TableMeta;

interface FieldValueGenerator {

    void generate(TableMeta<?> domainTable, RowWrapper wrapper);

    void validate(TableMeta<?> domainTable, RowWrapper wrapper);


}
