package io.army.criteria.impl.inner;

import java.util.List;

@DeveloperForbid
public interface InnerBatchUpdate extends InnerUpdate {

    List<Object> namedParamList();

}
