package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class ValueInsert<C, T extends IDomain, OR, IR, SR> extends AbstractInsert<C, T, IR> implements
        Insert._OptionClause<OR>, Insert._CommonExpClause<C, T, SR>, Insert._ValueClause<C, T>
        , Insert._PreferLiteralOptionClause<OR>, _ValuesInsert {


    private boolean optimizingParam;

    private boolean migration;

    private NullHandleMode nullHandleMode = NullHandleMode.INSERT_DEFAULT;

    private boolean prepared;

    private Map<FieldMeta<?>, _Expression> commonExpMap;

    private List<IDomain> domainList;

    ValueInsert(TableMeta<T> table, CriteriaContext criteriaContext) {
        super(table, criteriaContext);
        if (this instanceof SubStatement) {
            CriteriaContextStack.push(this.criteriaContext);
        } else {
            CriteriaContextStack.setContextStack(this.criteriaContext);
        }

    }

    @Override
    public final boolean isPrepared() {
        return this.prepared;
    }

    @Override
    public final void prepared() {
        _Assert.prepared(this.prepared);
    }


    @Override
    public final _OptionClause<OR> preferLiteral(boolean prefer) {
        this.optimizingParam = prefer;
        return this;
    }

    @Override
    public final OR migration() {
        this.migration = true;
        this.nullHandleMode = NullHandleMode.INSERT_NULL;
        return (OR) this;
    }

    @Override
    public final OR nullHandle(NullHandleMode mode) {
        Objects.requireNonNull(mode);
        this.nullHandleMode = mode;
        return (OR) this;
    }

    @Override
    public final SR set(final FieldMeta<? super T> field, final @Nullable Object paramOrExp) {
        if (!field.insertable()) {
            throw _Exceptions.nonInsertableField(field);
        }
        if (!this.migration) {
            final String fieldName = field.fieldName();
            if (field.generatorType() != null
                    || _MetaBridge.UPDATE_TIME.equals(fieldName)
                    || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(field);
            }
        }
        Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            commonExpMap = new HashMap<>();
            this.commonExpMap = commonExpMap;
        }
        final Expression exp;
        if (paramOrExp == null) {
            if (this.optimizingParam) {
                exp = SQLs.nullWord();
            } else {
                exp = SQLs.StringTypeNull.INSTANCE;
            }
        } else if (paramOrExp instanceof SubQuery && !(paramOrExp instanceof ScalarExpression)) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) paramOrExp);
        } else if (paramOrExp instanceof Expression) {
            exp = (Expression) paramOrExp;
        } else {
            exp = SQLs.param(field, paramOrExp);
        }
        if (commonExpMap.putIfAbsent(field, (ArmyExpression) exp) != null) {
            String m = String.format("duplication common expression for %s.", field);
            throw new CriteriaException(m);
        }
        return (SR) this;
    }

    @Override
    public final SR setExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function) {
        final Expression expression;
        expression = function.apply(this.criteria);
        assert expression != null;
        return this.set(field, expression);
    }

    @Override
    public final SR setExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        assert expression != null;
        return this.set(field, expression);
    }

    @Override
    public final SR setDefault(FieldMeta<? super T> field) {
        return this.set(field, SQLs.defaultWord());
    }

    @Override
    public final SR setNull(FieldMeta<? super T> field) {
        return this.set(field, SQLs.nullWord());
    }


    @Override
    public final SR ifSetExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function) {
        final Expression expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.set(field, expression);
        }
        return (SR) this;
    }

    @Override
    public final SR ifSetExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression != null) {
            this.set(field, expression);
        }
        return (SR) this;
    }

    @Override
    public final _InsertSpec value(T domain) {
        Objects.requireNonNull(domain);
        this.domainList = Collections.singletonList(domain);
        return this;
    }

    @Override
    public final _InsertSpec value(Function<C, T> function) {
        return this.value(function.apply(this.criteria));
    }

    @Override
    public final _InsertSpec value(Supplier<T> supplier) {
        return this.value(supplier.get());
    }

    @Override
    public final _InsertSpec value(Function<String, Object> function, String keyName) {
        final Object domain;
        domain = function.apply(keyName);
        if (domain == null || domain.getClass() != this.table.javaType()) {
            throw domainTypeNotMatch(domain);
        }
        return this.value((T) domain);
    }

    @Override
    public final _InsertSpec values(List<T> domainList) {
        this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
        return this;
    }

    @Override
    public final _InsertSpec values(Function<C, List<T>> function) {
        return this.values(function.apply(this.criteria));
    }

    @Override
    public final _InsertSpec values(Supplier<List<T>> supplier) {
        return this.values(supplier.get());
    }

    @Override
    public final _InsertSpec values(Function<String, Object> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof List)) {
            String m = String.format("%s return isn't %s.", Function.class.getName(), List.class.getName());
            throw new CriteriaException(m);
        }
        final List<?> domainList = (List<?>) value;
        final Class<T> javaType = this.table.javaType();

        final List<IDomain> list = new ArrayList<>(domainList.size());
        for (Object domain : domainList) {
            if (domain == null || domain.getClass() != javaType) {
                throw domainTypeNotMatch(domain);
            }
            list.add((IDomain) domain);
        }
        this.domainList = Collections.unmodifiableList(list);
        return this;
    }

    @Override
    public final Insert asInsert() {
        _Assert.nonPrepared(this.prepared);
        this.criteriaContext.clear();
        if (this instanceof SubStatement) {
            CriteriaContextStack.pop(this.criteriaContext);
        } else {
            CriteriaContextStack.clearContextStack(this.criteriaContext);
        }
        if (this.domainList == null) {
            throw _Exceptions.castCriteriaApi();
        }
        final Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            this.commonExpMap = Collections.emptyMap();
        } else {
            this.commonExpMap = Collections.unmodifiableMap(commonExpMap);
        }
        this.onAsInsert();
        this.prepared = true;
        return this;
    }

    @Override
    public final boolean isMigration() {
        return this.migration;
    }

    @Override
    public final NullHandleMode nullHandle() {
        final NullHandleMode mode = this.nullHandleMode;
        assert mode != null;
        return mode;
    }

    @Override
    public final boolean isPreferLiteral() {
        return this.optimizingParam;
    }

    @Override
    public final Map<FieldMeta<?>, _Expression> commonExpMap() {
        prepared();
        return this.commonExpMap;
    }

    @Override
    public final List<IDomain> domainList() {
        return this.domainList;
    }

    @Override
    public final void clear() {
        super.clear();
        this.prepared = false;
        this.migration = false;
        this.commonExpMap = null;
        this.domainList = null;
        this.onClear();
    }

    void onClear() {

    }

    void onAsInsert() {

    }

    private CriteriaException domainTypeNotMatch(@Nullable Object domain) {
        final String m;
        if (domain == null) {
            m = String.format("null isn't %s type.", this.table.javaType().getName());
        } else {
            m = String.format("%s isn't %s type.", domain.getClass().getName(), this.table.javaType().getName());
        }
        return new CriteriaException(m);
    }


}
