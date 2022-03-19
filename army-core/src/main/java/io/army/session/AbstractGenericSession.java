package io.army.session;


import io.army.cache.DomainUpdateAdvice;
import io.army.criteria.CriteriaException;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Assert;

import java.util.Collections;
import java.util.List;

public abstract class AbstractGenericSession implements GenericSession {

    public static boolean cacheDomainUpdate(_SingleUpdate update) {
        throw new CriteriaException("");
    }

    protected static abstract class CacheDomainUpdate implements Update, _SingleUpdate {

        public static CacheDomainUpdate build(DomainUpdateAdvice advice) {

            throw new UnsupportedOperationException();
        }

        private final TableMeta<?> tableMeta;

        private final String tableAlias;

        private List<FieldMeta<?>> targetFieldList;

        private List<_Expression> valueExpList;

        private List<_Predicate> predicateList;

        private boolean prepared;

        private CacheDomainUpdate(DomainUpdateAdvice advice, List<FieldMeta<?>> targetFieldList
                , List<_Expression> valueExpList) {

            this.tableMeta = null;
            this.tableAlias = "t";

            this.targetFieldList = Collections.unmodifiableList(targetFieldList);
            this.valueExpList = Collections.unmodifiableList(valueExpList);
            this.predicateList = advice.predicateList();

            this.prepared = true;
        }

        @Override
        public boolean isPrepared() {
            return false;
        }

        @Override
        public TableMeta<?> table() {
            return this.tableMeta;
        }


        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public List<FieldMeta<?>> fieldList() {
            return this.targetFieldList;
        }

        @Override
        public List<_Expression> valueExpList() {
            return this.valueExpList;
        }

        @Override
        public List<_Predicate> predicateList() {
            return this.predicateList;
        }

        @Override
        public void clear() {
            this.targetFieldList = null;
            this.valueExpList = null;
            this.predicateList = null;
            this.prepared = false;
        }

        @Override
        public void prepared() {
            _Assert.prepared(this.prepared);
        }
    }

}
