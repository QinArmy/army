package io.army.criteria.impl.inner;

import java.util.List;

@DeveloperForbid
public interface InnerStandardBatchDelete extends InnerBatchDelete, InnerStandardDelete {

    List<Object> namedParamList();
}
