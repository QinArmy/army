package io.army.criteria.impl.inner;


@DeveloperForbid
public interface InnerStandardDomainUpdate extends InnerStandardSingleUpdate, InnerDomainUpdate
        , InnerStandardDomainDML {

    Object primaryKeyValue();

}
