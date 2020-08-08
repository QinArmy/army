package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface DDLContext {

    StringBuilder sqlBuilder();

    TableMeta<?> tableMeta();

    void appendTable();

    void appendField(FieldMeta<?, ?> fieldMeta);

    void append(String sql);

    void resetBuilder();

    /**
     * @return a unmodifiable list
     */
    List<String> build();

}
