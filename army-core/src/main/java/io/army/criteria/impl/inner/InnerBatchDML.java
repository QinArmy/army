package io.army.criteria.impl.inner;

import io.army.beans.ReadonlyWrapper;

import java.util.List;

@DeveloperForbid
public interface InnerBatchDML extends InnerSQL {

    List<ReadonlyWrapper> namedParamList();
}
