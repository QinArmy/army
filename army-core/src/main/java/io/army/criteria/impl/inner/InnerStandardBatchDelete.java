package io.army.criteria.impl.inner;

import java.util.List;

@DeveloperForbid
public interface InnerStandardBatchDelete extends InnerStandardDelete, InnerDelete {

    List<Object> namedParamList();
}
