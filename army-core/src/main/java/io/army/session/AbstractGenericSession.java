package io.army.session;


import io.army.beans.DomainReadonlyWrapper;
import io.army.cache.DomainUpdateAdvice;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.criteria.impl.Sqls;
import io.army.criteria.impl.inner.InnerStandardUpdate;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class AbstractGenericSession implements GenericSession {

    public static boolean cacheDomainUpdate(InnerStandardUpdate update) {
        return update instanceof AbstractGenericSession.CacheDomainUpdate;
    }

    protected static final class CacheDomainUpdate implements Update, InnerStandardUpdate {

        public static CacheDomainUpdate build(DomainUpdateAdvice advice) {

            final Set<FieldMeta<?, ?>> set = advice.targetFieldSet();

            List<FieldMeta<?, ?>> targetList = new ArrayList<>(set.size());
            List<Expression<?>> valueList = new ArrayList<>(set.size());
            DomainReadonlyWrapper readonlyWrapper = advice.readonlyWrapper();

            for (FieldMeta<?, ?> fieldMeta : set) {
                targetList.add(fieldMeta);
                Object value = readonlyWrapper.getPropertyValue(fieldMeta.fieldName());
                if (value == null) {
                    valueList.add(Sqls.asNull(fieldMeta.mappingMeta()));
                } else {
                    valueList.add(Sqls.param(value, fieldMeta));
                }
            }
            return new CacheDomainUpdate(advice, targetList, valueList);
        }

        private final TableMeta<?> tableMeta;

        private final String tableAlias;

        private List<FieldMeta<?, ?>> targetFieldList;

        private List<Expression<?>> valueExpList;

        private List<IPredicate> predicateList;

        private boolean prepared;

        private CacheDomainUpdate(DomainUpdateAdvice advice, List<FieldMeta<?, ?>> targetFieldList
                , List<Expression<?>> valueExpList) {

            this.tableMeta = advice.readonlyWrapper().tableMeta();
            this.tableAlias = "t";

            this.targetFieldList = Collections.unmodifiableList(targetFieldList);
            this.valueExpList = Collections.unmodifiableList(valueExpList);
            this.predicateList = advice.predicateList();

            this.prepared = true;
        }


        @Override
        public final TableMeta<?> tableMeta() {
            return this.tableMeta;
        }

        @Override
        public final String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public final int databaseIndex() {
            return -1;
        }

        @Override
        public final int tableIndex() {
            return -1;
        }

        @Override
        public final List<FieldMeta<?, ?>> targetFieldList() {
            return this.targetFieldList;
        }

        @Override
        public final List<Expression<?>> valueExpList() {
            return this.valueExpList;
        }

        @Override
        public final List<IPredicate> predicateList() {
            return this.predicateList;
        }

        @Override
        public final void clear() {
            this.targetFieldList = null;
            this.valueExpList = null;
            this.predicateList = null;
            this.prepared = false;
        }

        @Override
        public final boolean prepared() {
            return this.prepared;
        }
    }

}
