package io.army.criteria.impl.inner;


@DeveloperForbid
public interface InnerStandardDomainUpdate extends InnerStandardUpdate, InnerDomainUpdate
        , InnerStandardDomainDML {

    Object primaryKeyValue();

}