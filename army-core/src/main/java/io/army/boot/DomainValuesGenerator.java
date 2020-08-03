package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface DomainValuesGenerator {

    DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain, boolean migrationData)
            throws FieldValuesCreateException;

    void createValues(DomainWrapper domainWrapper, boolean migrationData);


    static DomainValuesGenerator build(GenericSessionFactory sessionFactory) {
        return new DomainValuesGeneratorImpl(sessionFactory);
    }
}
