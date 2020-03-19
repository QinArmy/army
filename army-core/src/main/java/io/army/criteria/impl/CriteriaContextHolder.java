package io.army.criteria.impl;

import io.army.criteria.Select;
import org.springframework.core.NamedThreadLocal;


 abstract class CriteriaContextHolder {

     private static final NamedThreadLocal<CriteriaContext> HOLDER = new NamedThreadLocal<>("CriteriaContext holder");

    private CriteriaContextHolder() {
        throw new UnsupportedOperationException();
    }


    static void setContext(CriteriaContext context) {
        HOLDER.set(context);
    }

    static CriteriaContext getContext() {
        CriteriaContext context = HOLDER.get();
        if(context == null){
            throw new IllegalStateException(String.format("thread[%s] no CriteriaContext,criteria state error."
                    , Thread.currentThread().getName()));
        }
        return context;
    }

     /**
      * @see Select.SelectAble#asSelect()
      */
    static void clearContext(CriteriaContext context) {
        CriteriaContext current = HOLDER.get();
        if(current != context){
           throw  new IllegalStateException( String.format("thread[%s]  CriteriaContext not match,criteria state error."
                   , Thread.currentThread().getName()));
        }
        HOLDER.remove();
    }


}
