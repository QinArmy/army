package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.beans.BeanWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface FieldValuesGenerator {

    BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity)
            throws FieldValuesCreateException;

    BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity, boolean noDependValueAbort)
            throws FieldValuesCreateException;

    static FieldValuesGenerator build(GenericSessionFactory sessionFactory) {
        return null;
    }
}
