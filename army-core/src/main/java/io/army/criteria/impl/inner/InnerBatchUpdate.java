package io.army.criteria.impl.inner;

import java.util.Collection;

@DeveloperForbid
public interface InnerBatchUpdate extends InnerUpdate {

    Collection<Object> namedParams();

}
