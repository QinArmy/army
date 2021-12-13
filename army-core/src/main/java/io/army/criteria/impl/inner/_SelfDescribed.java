package io.army.criteria.impl.inner;

import io.army.dialect._SqlContext;

public interface _SelfDescribed {

    /**
     * This method has below step:
     * <ol>
     *     <li>append a space</li>
     *     <li>append SelfDescribed instance content in sql</li>
     * </ol>
     */
    void appendSql(_SqlContext context);

}
