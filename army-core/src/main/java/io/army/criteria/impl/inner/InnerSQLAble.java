package io.army.criteria.impl.inner;


public interface InnerSQLAble {

    /**
     * must invoke {@code io.army.criteria.impl.CriteriaContextHolder#clearContext(CriteriaContext)}
     */
    void prepare();
}
