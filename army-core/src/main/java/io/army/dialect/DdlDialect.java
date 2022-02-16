package io.army.dialect;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;

import java.util.List;

interface DdlDialect {

    List<String> errorMsgList();

    <T extends IDomain> void createTable(TableMeta<T> table, List<String> sqlList);

    <T extends IDomain> void addColumn(List<FieldMeta<T, ?>> fieldList, List<String> sqlList);

    void modifyTableComment(TableMeta<?> table, List<String> sqlList);

    void modifyColumn(List<_FieldResult> resultList, List<String> sqlList);

    <T extends IDomain> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

      <T extends IDomain> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);

    <T extends IDomain> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList);
}
