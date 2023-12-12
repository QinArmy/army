package io.army.dialect;


import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.schema._SchemaResult;

import java.util.List;

/**
 * <p>
 * This interface representing ddl parser. The result of this interface will be used by session factory for updating schema.
*
 * @see DialectParser#schemaDdl(_SchemaResult)
 * @since 1.0
 */
interface DdlParser {

    /**
     * If non-empty,then the result of this interface couldn't bee used. {@link DialectParser#schemaDdl(_SchemaResult)} must throw {@link io.army.meta.MetaException}
     */
    List<String> errorMsgList();

    void dropTable(List<TableMeta<?>> tableList, List<String> sqlList);

    <T> void createTable(TableMeta<T> table, List<String> sqlList);

    void addColumn(List<FieldMeta<?>> fieldList, List<String> sqlList);

    void modifyTableComment(TableMeta<?> table, List<String> sqlList);

    void modifyColumn(List<_FieldResult> resultList, List<String> sqlList);

    <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

    <T> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

    <T> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);
}
