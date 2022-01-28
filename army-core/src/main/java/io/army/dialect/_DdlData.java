package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.IndexMeta;
import io.army.meta.TableMeta;

import java.util.List;

public final class _DdlData {

    List<TableMeta<?>> newTableList;

    public static final class TableData {

        private boolean comment;

        private List<FieldMeta<?, ?>> addFieldList;

        private List<FieldMeta<?, ?>> changeFieldList;

        private List<IndexMeta<?>> addIndexList;

        private List<IndexMeta<?>> changeIndexList;

    }

}
