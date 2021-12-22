package io.army.session;


import io.army.beans.DomainReadonlyWrapper;
import io.army.cache.DomainUpdateAdvice;
import io.army.criteria.CriteriaException;
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
        throw new CriteriaException("");
    }

    protected static final class CacheDomainUpdate implements Update, _SingleUpdate {

        public static CacheDomainUpdate build(DomainUpdateAdvice advice) {

            final Set<FieldMeta<?, ?>> set = advice.targetFieldSet();

            List<FieldMeta<?, ?>> targetList = new ArrayList<>(set.size());
            List<_Expression<?>> valueList = new ArrayList<>(set.size());
            DomainReadonlyWrapper readonlyWrapper = advice.readonlyWrapper();

            for (FieldMeta<?, ?> field : set) {
                targetList.add(field);
                Object value = readonlyWrapper.get(field.fieldName());
                if (value == null) {
                    valueList.add((_Expression<?>) SQLs.optimizingParam(field, null));
                } else {
                    valueList.add((_Expression<?>) SQLs.param(field, value));
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
        public byte databaseIndex() {
            // always negative
            return -1;
        }

        @Override
        public byte tableIndex() {
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
