package io.army.criteria.impl;

import io.army.util.Assert;
import org.springframework.core.NamedThreadLocal;


abstract class CriteriaContextHolder {

    private static final NamedThreadLocal<CriteriaContext> HOLDER = new NamedThreadLocal<>("criteria sql accessor.");

    private CriteriaContextHolder() {
        throw new UnsupportedOperationException();
    }


    static void setContext(CriteriaContext context) {
        Assert.state(HOLDER.get() == null, () -> String.format("thread[%s] CriteriaContext error"
                , Thread.currentThread().getName()));

        HOLDER.set(context);
    }

    static CriteriaContext getContext() {
        CriteriaContext context = HOLDER.get();
        Assert.state(context != null, () -> String.format("thread[%s] no CriteriaContext"
                , Thread.currentThread().getName()));
        return context;
    }

    static void clearContext(CriteriaContext context) {
        CriteriaContext current = HOLDER.get();
        Assert.state(current == context, () -> String.format("thread[%s]  CriteriaContext not match"
                , Thread.currentThread().getName()));

        HOLDER.remove();
    }


}
