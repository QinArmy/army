package io.army.criteria.impl;

import io.army.criteria.CriteriaException;


abstract class CriteriaContextHolder {

    private static final ThreadLocal<CriteriaContext> HOLDER = new ThreadLocal<>();

    private CriteriaContextHolder() {
        throw new UnsupportedOperationException();
    }


    static void setContext(CriteriaContext context) {
        HOLDER.set(context);
    }

    static CriteriaContext getContext() {
        CriteriaContext context = HOLDER.get();
        if (context == null) {
            throw new IllegalStateException(String.format("thread[%s] no CriteriaContext,criteria state error."
                    , Thread.currentThread().getName()));
        }
        return context;
    }

    static void clearContext(CriteriaContext context) {
        final CriteriaContext current = HOLDER.get();
        if (current != context) {
            throw new CriteriaException(String.format("thread[%s]  CriteriaContext not match,criteria state error."
                    , Thread.currentThread().getName()));
        }
        HOLDER.remove();
        current.clear();
    }


}
