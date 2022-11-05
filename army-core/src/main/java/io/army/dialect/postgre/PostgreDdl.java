package io.army.dialect.postgre;

import io.army.dialect.ArmyParser;
import io.army.dialect._DdlDialect;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.schema._FieldResult;
import io.army.sqltype.SqlType;

import java.util.List;

final class PostgreDdl extends _DdlDialect {

    static PostgreDdl create(ArmyParser dialect) {
        return new PostgreDdl(dialect);
    }

    private PostgreDdl(ArmyParser dialect) {
        super(dialect);
    }

    @Override
    public void modifyTableComment(TableMeta<?> table, List<String> sqlList) {

    }

    @Override
    public void modifyColumn(List<_FieldResult> resultList, List<String> sqlList) {

    }

    @Override
    public <T> void createIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void changeIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    public <T> void dropIndex(TableMeta<T> table, List<String> indexNameList, List<String> sqlList) {

    }

    @Override
    protected void dataType(FieldMeta<?> field, SqlType type, StringBuilder builder) {

    }

    @Override
    protected void appendPostGenerator(FieldMeta<?> field, StringBuilder builder) {

    }

    @Override
    protected void appendTableOption(TableMeta<?> table, StringBuilder builder) {

    }
}
