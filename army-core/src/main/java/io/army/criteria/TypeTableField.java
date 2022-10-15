package io.army.criteria;

import io.army.meta.TableMeta;

public interface TypeTableField<T> extends TableField {

    @Override
    TableMeta<T> tableMeta();


}
