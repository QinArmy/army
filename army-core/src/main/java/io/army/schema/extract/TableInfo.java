package io.army.schema.extract;

import java.util.List;

/**
 * 代表数据库表的实际 元数据
 */
public interface TableInfo {

    SchemaInfo schema();

    String name();

    /**
     * Get the comments/remarks defined for the table.
     *
     * @return The table comments
     */
     String comment();

     List<ColumnInfo> columns();

     List<IndexInfo> indexes();

    boolean physicalTable();

}
