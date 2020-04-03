package io.army.criteria.inner.postgre;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerQuery;
import io.army.criteria.postgre.PostgreWindow;

import java.util.List;

@DeveloperForbid
public interface PostgreInnerQuery extends InnerQuery {

    /**
     * @return a unmodifiable list
     */
    List<Expression<?>> distinctOnExpList();

    /**
     * @return a unmodifiable list
     */
    List<PostgreWindow> windowList();

    /**
     * @return a unmodifiable list
     */
    List<PostgreLockWrapper> lockWrapperList();

}
