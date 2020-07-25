package io.army.criteria.impl.inner;

import io.army.beans.DomainWrapper;

import java.util.List;

@DeveloperForbid
public interface InnerStandardBatchInsert extends InnerStandardInsert, InnerBatchDML {

    @Override
    List<DomainWrapper> wrapperList();

}
