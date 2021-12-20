package io.army.session;


import io.army.beans.DomainReadonlyWrapper;
import io.army.cache.DomainUpdateAdvice;
import io.army.criteria.Update;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractGenericSession implements GenericSession {

    public static boolean cacheDomainUpdate(_SingleUpdate update) {
        return update instanceof AbstractGenericSession.CacheDomainUpdate;
    }

    protected static final class CacheDomainUpdate implements Update, _SingleUpdate {

        public static CacheDomainUpdate build(DomainUpdateAdvice advice) {

            final Set<FieldMeta<?, ?>> set = advice.targetFieldSet();

            List<FieldMeta<?, ?>> targetList = new ArrayList<>(set.size());
            List<_Expression<?>> valueList = new ArrayList<>(set.size());
            DomainReadonlyWrapper readonlyWrapper = advice.readonlyWrapper();

            for (FieldMeta<?, ?> fieldMeta : set) {
                targetList.add(fieldMeta);
                Object value = readonlyWrapper.get(fieldMeta.fieldName());
                if (value == null) {
                    valueList.add((_Expression<?>) SQLs.nullWord());
                } else {
                    valueList.add((_Expression<?>) SQLs.param(fieldMeta, value));
                }
            }
            return new CacheDomainUpdate(advice, targetList, valueList);
        }

        private final TableMeta<?> tableMeta;

        private final String tableAlias;

        private List<FieldMeta<?, ?>> targetFieldList;

        private List<_Expression<?>> valueExpList;

        private List<_Predicate> predicateList;

        private boolean prepared;

        private CacheDomainUpdate(DomainUpdateAdvice advice, List<FieldMeta<?, ?>> targetFieldList
                , List<_Expression<?>> valueExpList) {

            this.tableMeta = advice.readonlyWrapper().tableMeta();
            this.tableAlias = "t";

            this.targetFieldList = Collections.unmodifiableList(targetFieldList);
            this.valueExpList = Collections.unmodifiableList(valueExpList);
            this.predicateList = advice.predicateList();

            this.prepared = true;
        }


        @Override
        public TableMeta<?> table() {
            return this.tableMeta;
        }

        @Override
        public int databaseIndex() {
            // always negative
            return -1;
        }

        @Override
        public int tableIndex() {
            // always negative
            return -1;
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public List<FieldMeta<?, ?>> fieldList() {
            return this.targetFieldList;
        }

        @Override
        public List<_Expression<?>> valueExpList() {
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
            Assert.prepared(this.prepared);
        }
    }

}
