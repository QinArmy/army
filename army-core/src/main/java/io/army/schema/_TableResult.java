package io.army.schema;

import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

/**
 * @see _SchemaResult
 */
public interface _TableResult {

    TableMeta<?> table();

    boolean comment();

    List<FieldMeta<? extends IDomain>> newFieldList();

    List<_FieldResult> changeFieldList();

    List<String> newIndexList();

    List<String> changeIndexList();

    static Builder builder() {
        return TableResultImpl.createBuilder();
    }


    interface Builder {

        void table(TableMeta<?> table);

        void appendNewColumn(FieldMeta<?> field);

        void comment(boolean comment);

        void appendFieldResult(_FieldResult fieldResult);

        void appendNewIndex(String indexName);

        void appendChangeIndex(String indexName);


        _TableResult buildAndClear();

    }


}
