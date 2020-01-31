package io.army.generator;

import io.army.SessionFactory;
import io.army.beans.BeanWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface FieldValuesGenerator {

    BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity)
            throws FieldValuesCreateException;

    static FieldValuesGenerator build(SessionFactory sessionFactory){
        return new FieldValuesGeneratorImpl(sessionFactory);
    }
}
