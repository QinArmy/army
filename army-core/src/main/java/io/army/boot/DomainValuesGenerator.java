package io.army.boot;

import io.army.GenericRmSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;

public interface DomainValuesGenerator {

    DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain, boolean migrationData)
            throws FieldValuesCreateException;

    void createValues(DomainWrapper domainWrapper, boolean migrationData);


    static DomainValuesGenerator build(GenericRmSessionFactory sessionFactory) {
        return new DomainValuesGeneratorImpl(sessionFactory);
    }
}
