package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

public interface DDLContext {

    SQLBuilder sqlBuilder();

    TableMeta<?> tableMeta();

    void appendTable();

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendFieldWithTable(FieldMeta<?, ?> fieldMeta);

    void appendSQL(String sql);

    void appendIdentifier(String identifier);

    void resetBuilder();

    /**
     * @return a unmodifiable list
     */
    List<String> build();

}
