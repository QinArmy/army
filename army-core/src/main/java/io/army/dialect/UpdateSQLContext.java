package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

public interface UpdateSQLContext extends SQLContext {

    TableMeta<?> tableMeta();

    String safeAlias();

    void assertField(FieldMeta<?,?> targetField)throws CriteriaException;

    FieldMeta<?,?> versionField();

    FieldMeta<?,?> updateTimeField();

}
