package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface DDLContext {

    /**
     * @see io.army.modelgen.MetaConstant#MAYBE_NO_DEFAULT_TYPES
     */
    String defaultValue(FieldMeta<?, ?> fieldMeta);

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
