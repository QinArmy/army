package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface FieldValuesGenerator {

    DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain)
            throws FieldValuesCreateException;


    static FieldValuesGenerator build(GenericSessionFactory sessionFactory) {
        return new FieldValuesGeneratorImpl(sessionFactory);
    }
}
