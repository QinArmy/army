package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class ValueInsert<C, T extends IDomain, IR, VR> implements Insert, Insert._ComplexColumnListClause<C, T, IR>
        , Insert._ComplexColumnClause<T, IR>, Statement._RightParenClause<IR>, Insert._InsertSpec
        , _ValuesInsert, Insert._CommonExpClause<C, T, IR>, Insert._ValueClause<C, T, VR> {


    final CriteriaContext criteriaContext;

    final C criteria;

    private final boolean preferLiteral;

    private final boolean migration;

    private final NullHandleMode nullHandleMode;

    final TableMeta<T> table;

    private List<FieldMeta<?>> fieldList;

    private List<FieldMeta<?>> childFieldList;

    private Map<FieldMeta<?>, _Expression> commonExpMap;

    private List<IDomain> domainList;

    private boolean prepared;

    ValueInsert(ValueInsetOptionClause<C, ?, ?> optionClause, TableMeta<T> table) {
        this.criteriaContext = optionClause.criteriaContext;
        this.criteria = criteriaContext.criteria();
        this.table = table;
        this.preferLiteral = optionClause.preferLiteral;

        this.migration = optionClause.migration;
        this.nullHandleMode = optionClause.nullHandleMode;

    }


    @Override
    public final _RightParenClause<IR> leftParen(Consumer<Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this::addField);
        this.finishFieldList();
        return this;
    }

    @Override
    public final _RightParenClause<IR> leftParen(BiConsumer<C, Consumer<FieldMeta<? super T>>> consumer) {
        consumer.accept(this.criteria, this::addField);
        this.finishFieldList();
        return this;
    }

    @Override
    public final _ComplexColumnClause<T, IR> leftParen(FieldMeta<? super T> field) {
        this.addField(field);
        return this;
    }

    @Override
    public final _ComplexColumnClause<T, IR> comma(FieldMeta<? super T> field) {
        this.addField(field);
        return this;
    }

    @Override
    public final IR rightParen() {
        this.finishFieldList();
        return this.endColumnList();
    }


    @Override
    public final IR common(final FieldMeta<? super T> field, final @Nullable Object value) {
        if (!field.insertable()) {
            throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
        }
        final String fieldName = field.fieldName();
        if (_MetaBridge.UPDATE_TIME.equals(fieldName)
                || _MetaBridge.VERSION.equals(fieldName)
                || _MetaBridge.CREATE_TIME.equals(fieldName)) {
            String m = String.format("Common expression don't support %s", field);
            throw CriteriaContextStack.criteriaError(m);
        }
        if (!this.migration && field.generatorType() != null) {
            throw CriteriaContextStack.criteriaError(_Exceptions::armyManageField, field);
        }
        Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
        if (commonExpMap == null) {
            commonExpMap = new HashMap<>();
            this.commonExpMap = commonExpMap;
        }
        final Expression exp;
        if (value == null) {
            if (this.preferLiteral) {
                exp = SQLs.nullWord();
            } else {
                exp = SQLs._nullParam();
            }
        } else if (value instanceof SubQuery && !(value instanceof ScalarExpression)) {
            throw CriteriaContextStack.criteriaError(_Exceptions::nonScalarSubQuery, (SubQuery) value);
        } else if (value instanceof Expression) {
            exp = (Expression) value;
        } else {
            exp = SQLs.param(field, value);
        }
        if (commonExpMap.putIfAbsent(field, (ArmyExpression) exp) != null) {
            String m = String.format("duplication common expression for %s.", field);
            throw CriteriaContextStack.criteriaError(m);
        }
        return (IR) this;
    }

    @Override
    public final IR commonLiteral(FieldMeta<? super T> field, @Nullable Object value) {
        return this.common(field, SQLs._nullableLiteral(field, value));
    }

    @Override
    public final IR commonExp(FieldMeta<? super T> field, Function<C, ? extends Expression> function) {
        final Expression expression;
        expression = function.apply(this.criteria);
        if (expression == null) {
            throw CriteriaContextStack.criteriaError("return null,not expression.");
        }
        return this.common(field, expression);
    }

    @Override
    public final IR commonExp(FieldMeta<? super T> field, Supplier<? extends Expression> supplier) {
        final Expression expression;
        expression = supplier.get();
        if (expression == null) {
            throw CriteriaContextStack.criteriaError("return null,not expression.");
        }
        return this.common(field, expression);
    }

    @Override
    public final IR commonDefault(FieldMeta<? super T> field) {
        return this.common(field, SQLs.defaultWord());
    }

    @Override
    public final IR commonNull(FieldMeta<? super T> field) {
        return this.common(field, SQLs.nullWord());
    }


    @Override
    public final IR ifCommon(FieldMeta<? super T> field, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.common(field, value);
        }
        return (IR) this;
    }

    @Override
    public final IR ifCommon(FieldMeta<? super T> field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.common(field, value);
        }
        return (IR) this;
    }

    @Override
    public final IR ifCommon(FieldMeta<? super T> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.common(field, value);
        }
        return (IR) this;
    }

    @Override
    public final IR ifCommonLiteral(FieldMeta<? super T> field, Supplier<?> supplier) {
        final Object value;
        value = supplier.get();
        if (value != null) {
            this.common(field, SQLs._nonNullLiteral(field, value));
        }
        return (IR) this;
    }

    @Override
    public final IR ifCommonLiteral(FieldMeta<? super T> field, Function<C, ?> function) {
        final Object value;
        value = function.apply(this.criteria);
        if (value != null) {
            this.common(field, SQLs._nonNullLiteral(field, value));
        }
        return (IR) this;
    }

    @Override
    public final IR ifCommonLiteral(FieldMeta<? super T> field, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (value != null) {
            this.common(field, SQLs._nonNullLiteral(field, value));
        }
        return (IR) this;
    }

    @Override
    public final VR value(T domain) {
        CriteriaContextStack.assertNonNull(domain, "domain must non-null");
        this.domainList = Collections.singletonList(domain);
        return (VR) this;
    }

    @Override
    public final VR value(Function<C, T> function) {
        return this.value(function.apply(this.criteria));
    }

    @Override
    public final VR value(Supplier<T> supplier) {
        return this.value(supplier.get());
    }

    @Override
    public final VR value(Function<String, Object> function, String keyName) {
        final Object domain;
        domain = function.apply(keyName);
        if (domain == null || domain.getClass() != this.table.javaType()) {
            throw domainTypeNotMatch(domain);
        }
        return this.value((T) domain);
    }

    @Override
    public VR values(List<T> domainList) {
        final int size;
        if ((size = domainList.size()) == 0) {
            throw CriteriaContextStack.criteriaError("domainList must non-empty");
        }
        if (size == 1) {
            this.domainList = Collections.singletonList(domainList.get(0));
        } else {
            this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
        }
        return (VR) this;
    }

    @Override
    public final VR values(Function<C, List<T>> function) {
        return this.values(function.apply(this.criteria));
    }

    @Override
    public final VR values(Supplier<List<T>> supplier) {
        return this.values(supplier.get());
    }

    @Override
    public final VR values(Function<String, Object> function, String keyName) {
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
        return (VR) this;
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
    public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
        final _Dialect d;
        d = _MockDialects.from(dialect);
        return d.printStmt(d.insert(this, visible), none);
    }

    @Override
    public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
        return _MockDialects.from(dialect).insert(this, visible);
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
    public final boolean isMigration() {
        return this.migration;
    }

    @Override
    public final NullHandleMode nullHandle() {
        return this.nullHandleMode;
    }

    @Override
    public final boolean isPreferLiteral() {
        return this.preferLiteral;
    }


    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final List<FieldMeta<?>> fieldList() {
        List<FieldMeta<?>> fieldList = this.fieldList;
        if (fieldList == null) {
            fieldList = Collections.emptyList();
        }
        return fieldList;
    }

    @Override
    public final List<FieldMeta<?>> childFieldList() {
        List<FieldMeta<?>> childFieldList = this.childFieldList;
        if (childFieldList == null) {
            childFieldList = Collections.emptyList();
        }
        return childFieldList;
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
        this.prepared = false;
        this.fieldList = null;
        this.childFieldList = null;
        this.commonExpMap = null;

        this.domainList = null;
        this.onClear();
    }

    void onClear() {

    }

    void onAsInsert() {

    }

    abstract IR endColumnList();


    private void addField(final FieldMeta<?> field) {
        if (!field.insertable()) {
            throw CriteriaContextStack.criteriaError(_Exceptions::nonInsertableField, field);
        }
        final TableMeta<?> fieldTable = field.tableMeta();
        final TableMeta<?> table = this.table;

        if (fieldTable instanceof ChildTableMeta) {
            if (fieldTable != table) {
                throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
            }
            List<FieldMeta<?>> childFieldList = this.childFieldList;
            if (childFieldList == null) {
                childFieldList = new ArrayList<>();
            }
            childFieldList.add(field);
        } else if (fieldTable == table
                || (table instanceof ChildTableMeta && fieldTable == ((ChildTableMeta<?>) table).parentMeta())) {
            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = new ArrayList<>();
            }
            fieldList.add(field);
        } else {
            throw CriteriaContextStack.criteriaError(_Exceptions::unknownColumn, field);
        }


    }


    private void finishFieldList() {
        final List<FieldMeta<?>> fieldList, childFieldList;
        fieldList = this.fieldList;
        childFieldList = this.childFieldList;
        if ((fieldList == null || fieldList.size() == 0) && (childFieldList == null || childFieldList.size() == 0)) {
            throw CriteriaContextStack.criteriaError("Column list must not empty.");
        }
        if (fieldList == null) {
            this.fieldList = Collections.emptyList();
        } else {
            this.fieldList = Collections.unmodifiableList(fieldList);
        }
        if (childFieldList == null) {
            this.childFieldList = Collections.emptyList();
        } else {
            this.childFieldList = Collections.unmodifiableList(childFieldList);
        }

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
