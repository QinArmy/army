package io.army.boot;

import io.army.bean.DomainWrapper;
import io.army.bean.ObjectWrapper;
import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.session.GenericSessionFactory;

public interface DomainValuesGenerator {

    default DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain, boolean migrationData)
            throws FieldValuesCreateException {
        throw new UnsupportedOperationException();
    }

    void createValues(ObjectWrapper domainWrapper, boolean migrationData);


    static DomainValuesGenerator build(GenericSessionFactory sessionFactory) {
        return new DomainValuesGeneratorImpl(sessionFactory);
    }
}
