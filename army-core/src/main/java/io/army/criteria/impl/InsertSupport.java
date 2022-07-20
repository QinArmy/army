package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Dialect;
import io.army.dialect._DialectParser;
import io.army.dialect._DialectUtils;
import io.army.dialect._MockDialects;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.Stmt;
import io.army.util._Assert;
import io.army.util._ClassUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class InsertSupport {

    InsertSupport() {
        throw new UnsupportedOperationException();
    }


    enum FieldMode {
        IN_TABLE,
        IN_LIST,
        UNKNOWN
    }


    interface InsertOptions extends CriteriaContextSpec {


        boolean isMigration();

        @Nullable
        NullHandleMode nullHandle();


    }

    interface NonQueryInsertOptions extends InsertOptions {

        boolean isPreferLiteral();

    }


    interface ColumnListClause extends CriteriaContextSpec {

        void validateField(FieldMeta<?> field, @Nullable ArmyExpression value);


    }

    static abstract class InsertOptionsImpl<MR, NR> implements InsertOptions, Insert._MigrationOptionClause<MR>
            , Insert._NullOptionClause<NR> {

        final CriteriaContext criteriaContext;

        private boolean migration;

        private NullHandleMode nullHandleMode;

        InsertOptionsImpl(CriteriaContext criteriaContext) {
            this.criteriaContext = criteriaContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final MR migration(boolean migration) {
            this.migration = migration;
            return (MR) this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final NR nullHandle(NullHandleMode mode) {
            this.nullHandleMode = mode;
            return (NR) this;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final boolean isMigration() {
            return this.migration;
        }

        @Override
        public final NullHandleMode nullHandle() {
            return this.nullHandleMode;
        }


    }//InsertOptionsImpl


    static abstract class NonQueryInsertOptionsImpl<MR, NR, PR> extends InsertOptionsImpl<MR, NR>
            implements NonQueryInsertOptions, Insert._PreferLiteralClause<PR> {


        private boolean preferLiteral;

        NonQueryInsertOptionsImpl(CriteriaContext criteriaContext) {
            super(criteriaContext);
        }

        @SuppressWarnings("unchecked")
        @Override
        public final PR preferLiteral(boolean prefer) {
            this.preferLiteral = prefer;
            return (PR) this;
        }

        @Override
        public final boolean isPreferLiteral() {
            return this.preferLiteral;
        }


    }//NonQueryInsertOptionsImpl


    static abstract class ColumnsClause<C, T extends IDomain, RR>
            implements Insert._ColumnListClause<C, T, RR>, Insert._StaticColumnDualClause<T, RR>
            , _Insert, ColumnListClause, Statement.StatementMockSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean migration;

        final TableMeta<T> table;

        private List<FieldMeta<?>> fieldList;

        private Map<FieldMeta<?>, Boolean> fieldMap;

        ColumnsClause(CriteriaContext criteriaContext, boolean migration, @Nullable TableMeta<T> table) {
            if (table == null) {
                //validate for insertInto method
                throw CriteriaContextStack.nullPointer(criteriaContext);
            }
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.migration = migration;
            this.table = table;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(Consumer<Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(BiConsumer<C, Consumer<FieldMeta<T>>> consumer) {
            consumer.accept(this.criteria, this::addField);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> leftParen(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> leftParen(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.addField(field1);
            this.addField(field2);
            return this;
        }

        @Override
        public final Statement._RightParenClause<RR> comma(FieldMeta<T> field) {
            this.addField(field);
            return this;
        }

        @Override
        public final Insert._StaticColumnDualClause<T, RR> comma(FieldMeta<T> field1, FieldMeta<T> field2) {
            this.addField(field1);
            this.addField(field2);
            return this;
        }

        @Override
        public final RR rightParen() {
            final List<FieldMeta<?>> fieldList;
            fieldList = this.fieldList;
            if (fieldList == null) {
                this.fieldList = Collections.emptyList();
            } else if (fieldList.size() == 1) {
                this.fieldList = Collections.singletonList(fieldList.get(0));
            } else {
                this.fieldList = Collections.unmodifiableList(fieldList);
            }

            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                this.fieldMap = Collections.emptyMap();
            } else {
                this.fieldMap = Collections.unmodifiableMap(fieldMap);
            }
            return this.columnListEnd();
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
            } else if (fieldList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            Map<FieldMeta<?>, Boolean> map = this.fieldMap;
            if (map == null) {
                map = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return map;
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _DialectParser parser;
            parser = _MockDialects.from(dialect);
            final Stmt stmt;
            if (this instanceof Insert) {
                stmt = parser.insert((Insert) this, visible);
            } else if (this instanceof ReplaceInsert || this instanceof MergeInsert) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return parser.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            final _DialectParser parser;
            parser = _MockDialects.from(dialect);
            final Stmt stmt;
            if (this instanceof Insert) {
                stmt = parser.insert((Insert) this, visible);
            } else if (this instanceof ReplaceInsert || this instanceof MergeInsert) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return stmt;
        }


        @Override
        public void clear() {
            this.fieldList = null;
            this.fieldMap = null;
        }

        abstract RR columnListEnd();


        @Override
        public final void validateField(final FieldMeta<?> field, final @Nullable ArmyExpression value) {
            final Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap != null) {
                if (!fieldMap.containsKey(field)) {
                    throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
                }
            } else if (field.tableMeta() != this.table) {
                // don't contain parent filed
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
            } else if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
            }

            if (value != null && !field.nullable() && value.isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

        }

        final CriteriaException forbidField(FieldMeta<?> field, Function<FieldMeta<?>, CriteriaException> function) {
            return CriteriaContextStack.criteriaError(this.criteriaContext, function, field);
        }


        private void addField(final FieldMeta<T> field) {
            final TableMeta<?> table = this.table;
            if (field.tableMeta() != table) {
                //don't contain parent field
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::unknownColumn, field);
            }
            if (!this.migration) {
                _DialectUtils.checkInsertField(this.table, field, this::forbidField);
            }

            Map<FieldMeta<?>, Boolean> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = new HashMap<>();
                this.fieldMap = fieldMap;
            }
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            List<FieldMeta<?>> fieldList = this.fieldList;
            if (fieldList == null) {
                fieldList = new ArrayList<>();
                this.fieldList = fieldList;
            }
            fieldList.add(field);

        }

    }//ColumnsClause


    @SuppressWarnings("unchecked")
    static abstract class ColumnDefaultClause<C, T extends IDomain, RR> extends ColumnsClause<C, T, RR>
            implements Insert._ColumnDefaultClause<C, T, RR>, _Insert._ValuesSyntaxInsert {


        final boolean preferLiteral;

        final NullHandleMode nullHandleMode;

        private Map<FieldMeta<?>, _Expression> commonExpMap;

        ColumnDefaultClause(InsertOptions options, TableMeta<T> table) {
            super(options.getCriteriaContext(), options.isMigration(), table);
            if (options instanceof NonQueryInsertOptions) {
                this.preferLiteral = ((NonQueryInsertOptions) options).isPreferLiteral();
            } else {
                this.preferLiteral = false;
            }
            this.nullHandleMode = options.nullHandle();
        }

        @Override
        public final RR defaultValue(final FieldMeta<T> field, final @Nullable Object value) {
            final ArmyExpression valueExp;
            if (value == null) {
                if (this.preferLiteral) {
                    valueExp = (ArmyExpression) SQLs.nullWord();
                } else {
                    valueExp = (ArmyExpression) SQLs.param(field, null);
                }
            } else if (!(value instanceof Expression)) {
                valueExp = (ArmyExpression) SQLs.param(field, value);
            } else if (value instanceof ArmyExpression) {
                valueExp = (ArmyExpression) value;
            } else {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }

            this.validateField(field);
            if (!field.nullable() && valueExp.isNullValue()) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::nonNullField, field);
            }

            Map<FieldMeta<?>, _Expression> commonExpMap = this.commonExpMap;
            if (commonExpMap == null) {
                commonExpMap = new HashMap<>();
                this.commonExpMap = commonExpMap;
            }
            if (commonExpMap.putIfAbsent(field, valueExp) != null) {
                String m = String.format("duplication common expression for %s.", field);
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            return (RR) this;
        }


        @Override
        public final RR defaultLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.defaultValue(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final RR defaultExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression expression;
            expression = function.apply(this.criteria);
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final RR defaultExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression expression;
            expression = supplier.get();
            if (expression == null) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            return this.defaultValue(field, expression);
        }

        @Override
        public final RR defaultNull(FieldMeta<T> field) {
            return this.defaultValue(field, SQLs.nullWord());
        }


        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultValue(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, value);
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final RR ifDefaultLiteral(FieldMeta<T> field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.defaultValue(field, SQLs._nonNullLiteral(field, value));
            }
            return (RR) this;
        }

        @Override
        public final Map<FieldMeta<?>, _Expression> defaultExpMap() {
            Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                map = Collections.emptyMap();
                this.commonExpMap = map;
            } else if (map instanceof HashMap) {
                map = Collections.unmodifiableMap(map);
                this.commonExpMap = map;
            }
            return map;
        }


        @Override
        public void clear() {
            super.clear();
            this.commonExpMap = null;
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

        final void endColumnDefaultClause() {
            final Map<FieldMeta<?>, _Expression> map = this.commonExpMap;
            if (map == null) {
                this.commonExpMap = Collections.emptyMap();
            } else if (map instanceof HashMap) {
                this.commonExpMap = Collections.unmodifiableMap(map);
            }
        }


    }//CommonExpClause


    @SuppressWarnings("unchecked")
    static abstract class DomainValueClause<C, T extends IDomain, CR, VR>
            extends ColumnDefaultClause<C, T, CR> implements Insert._DomainValueClause<C, T, VR>
            , _Insert._DomainInsert {


        private List<IDomain> domainList;

        DomainValueClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR value(T domain) {
            CriteriaContextStack.assertNonNull(this.criteriaContext, domain, "domain must non-null");
            this.domainList = Collections.singletonList(domain);
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final VR value(Function<C, T> function) {
            return this.value(function.apply(this.criteriaContext.criteria()));
        }

        @Override
        public final VR value(Supplier<T> supplier) {
            return this.value(supplier.get());
        }

        @Override
        public final VR value(Function<String, Object> function, String keyName) {
            final Object domain;
            domain = function.apply(keyName);
            if (!this.table.javaType().isInstance(domain)) {
                throw nonDomainInstance(this.criteriaContext, domain, this.table);
            }
            return this.value((T) domain);
        }

        @Override
        public final VR values(final List<T> domainList) {
            CriteriaContextStack.assertNonNull(this.criteriaContext, domainList, "domainList must non-empty");
            if (domainList.size() == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "domainList must non-empty");
            }
            this.domainList = Collections.unmodifiableList(new ArrayList<>(domainList));
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }

        @Override
        public final VR values(Function<C, List<T>> function) {
            return this.values(function.apply(this.criteriaContext.criteria()));
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
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }

            final List<?> domainList = (List<?>) value;
            final int size = domainList.size();
            if (size == 0) {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "domainList must non-empty");
            }
            final TableMeta<?> table = this.table;
            if (table == null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final Class<?> javaType = table.javaType();
            final List<IDomain> list = new ArrayList<>(size);
            for (Object domain : domainList) {
                if (domain == null || !javaType.isAssignableFrom(domain.getClass())) {
                    throw nonDomainInstance(this.criteriaContext, domain, this.table);
                }
                list.add((IDomain) domain);
            }
            this.domainList = Collections.unmodifiableList(list);
            this.endColumnDefaultClause();
            return this.valuesEnd();
        }


        @Override
        public final List<IDomain> domainList() {
            final List<IDomain> list = this.domainList;
            assert list != null;
            return list;
        }

        @Override
        public void clear() {
            super.clear();
            this.domainList = null;
        }

        abstract VR valuesEnd();

    }//DomainValueClause


    static abstract class StaticColumnValuePairClause<C, T extends IDomain, VR>
            implements Insert._StaticValueLeftParenClause<C, T, VR>, Insert._StaticColumnValueClause<C, T, VR>
            , CriteriaContextSpec {

        final CriteriaContext criteriaContext;

        final C criteria;

        final BiConsumer<FieldMeta<?>, ArmyExpression> validator;

        private List<Map<FieldMeta<?>, _Expression>> rowValuesList;

        private Map<FieldMeta<?>, _Expression> rowValuesMap;

        StaticColumnValuePairClause(CriteriaContext criteriaContext
                , BiConsumer<FieldMeta<?>, ArmyExpression> validator) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.validator = validator;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParen(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> leftParenExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> comma(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableParam(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaLiteral(FieldMeta<T> field, @Nullable Object value) {
            this.innerAddValuePair(field, SQLs._nullableLiteral(field, value));
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            final Expression exp;
            exp = supplier.get();
            this.innerAddValuePair(field, exp);
            return this;
        }

        @Override
        public final Insert._StaticColumnValueClause<C, T, VR> commaExp(FieldMeta<T> field, Function<C, ? extends Expression> function) {
            final Expression exp;
            exp = function.apply(this.criteria);
            this.innerAddValuePair(field, exp);
            return this;
        }


        final void endCurrentRow() {
            final Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (!(currentRow instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowValuesList;
            if (rowValueList == null) {
                rowValueList = new ArrayList<>();
                this.rowValuesList = rowValueList;
            } else if (!(rowValueList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            rowValueList.add(Collections.unmodifiableMap(currentRow));
            this.rowValuesMap = null;
        }

        final List<Map<FieldMeta<?>, _Expression>> endValuesClause() {
            final Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (!(currentRow instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            List<Map<FieldMeta<?>, _Expression>> rowValueList = this.rowValuesList;

            if (rowValueList == null) {
                rowValueList = Collections.singletonList(Collections.unmodifiableMap(currentRow));
            } else if (rowValueList instanceof ArrayList) {
                rowValueList.add(Collections.unmodifiableMap(currentRow));
                rowValueList = Collections.unmodifiableList(rowValueList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.rowValuesList = rowValueList;
            this.rowValuesMap = null;
            return rowValueList;
        }


        private void innerAddValuePair(final FieldMeta<?> field, final @Nullable Expression value) {
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.validator.accept(field, (ArmyExpression) value);
            Map<FieldMeta<?>, _Expression> currentRow = this.rowValuesMap;
            if (currentRow == null) {
                currentRow = new HashMap<>();
                this.rowValuesMap = currentRow;
            }
            if (currentRow.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
        }


    }//StaticValueColumnClause


    static abstract class DynamicValueInsertValueClause<C, T extends IDomain, RR, VR>
            extends ColumnDefaultClause<C, T, RR> implements Insert._DynamicValuesClause<C, T, VR>
            , PairsConstructor<FieldMeta<T>> {

        private List<Map<FieldMeta<?>, _Expression>> valuePairList;

        private Map<FieldMeta<?>, _Expression> valuePairMap;


        DynamicValueInsertValueClause(InsertOptions options, TableMeta<T> table) {
            super(options, table);
        }

        @Override
        public final VR values(Consumer<PairsConstructor<FieldMeta<T>>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final VR values(BiConsumer<C, PairsConstructor<FieldMeta<T>>> consumer) {
            return this.multiRowValues(consumer);
        }

        @Override
        public final PairConsumer<FieldMeta<T>> row() {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap instanceof HashMap) {
                List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
                if (valuePairList == null) {
                    valuePairList = new ArrayList<>();
                    this.valuePairList = valuePairList;
                } else if (!(valuePairList instanceof ArrayList)) {
                    throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
                }
                valuePairList.add(Collections.unmodifiableMap(currentPairMap));
            } else if (currentPairMap != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairMap = new HashMap<>();
            return this;
        }

        @Override
        public final PairConsumer<FieldMeta<T>> accept(FieldMeta<T> field, @Nullable Object value) {
            return this.addValuePair(field, SQLs._nullableParam(field, value));
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptLiteral(FieldMeta<T> field, @Nullable Object value) {
            return this.addValuePair(field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final PairConsumer<FieldMeta<T>> acceptExp(FieldMeta<T> field, Supplier<? extends Expression> supplier) {
            return this.addValuePair(field, supplier.get());
        }

        @Override
        public void clear() {
            super.clear();
            this.valuePairList = null;
            this.valuePairMap = null;
        }

        /**
         * @param rowValuesList a unmodified list
         */
        abstract VR valueClauseEnd(List<Map<FieldMeta<?>, _Expression>> rowValuesList);


        private PairConsumer<FieldMeta<T>> addValuePair(final FieldMeta<?> field, final @Nullable Expression value) {
            final Map<FieldMeta<?>, _Expression> currentPairMap = this.valuePairMap;
            if (currentPairMap == null) {
                String m = String.format("Not found any row,please use %s.row() method create row."
                        , PairsConstructor.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (!(currentPairMap instanceof HashMap)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }

            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            this.validateField(field, (ArmyExpression) value);

            if (currentPairMap.putIfAbsent(field, (ArmyExpression) value) != null) {
                throw duplicationValuePair(this.criteriaContext, field);
            }
            return this;
        }


        @SuppressWarnings("unchecked")
        private VR multiRowValues(final Object callback) {
            //1. validate
            if (this.valuePairMap != null || this.valuePairList != null) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            //2. callback
            if (callback instanceof Consumer) {
                ((Consumer<PairsConstructor<FieldMeta<T>>>) callback).accept(this);
            } else if (callback instanceof BiConsumer) {
                ((BiConsumer<C, PairsConstructor<FieldMeta<T>>>) callback).accept(this.criteria, this);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }

            Map<FieldMeta<?>, _Expression> valuePairMap = this.valuePairMap;
            if (valuePairMap == null) {
                String m = "Values insert must have one row at least";
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (valuePairMap.size() == 0) {
                valuePairMap = Collections.emptyMap();
            } else {
                valuePairMap = Collections.unmodifiableMap(valuePairMap);
            }
            this.valuePairMap = valuePairMap;
            List<Map<FieldMeta<?>, _Expression>> valuePairList = this.valuePairList;
            if (valuePairList == null) {
                valuePairList = Collections.singletonList(valuePairMap);
            } else if (valuePairList instanceof ArrayList) {
                valuePairList.add(valuePairMap);
                valuePairList = Collections.unmodifiableList(valuePairList);
            } else {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            this.valuePairList = valuePairList;
            return this.valueClauseEnd(valuePairList);
        }


    }//ValueInsertValueClause

    @SuppressWarnings("unchecked")
    static abstract class AssignmentSetClause<C, F extends TableField, SR>
            implements Insert._AssignmentSetClause<C, F, SR> {


        final CriteriaContext criteriaContext;

        final C criteria;

        final boolean supportRowItem;

        private List<ItemPair> itemPairList;

        AssignmentSetClause(CriteriaContext criteriaContext, boolean supportRowItem) {
            this.criteriaContext = criteriaContext;
            this.criteria = criteriaContext.criteria();
            this.supportRowItem = supportRowItem;
        }


        @Override
        public final SR setPair(Consumer<Consumer<ItemPair>> consumer) {
            consumer.accept(this::innerAddItemPair);
            return (SR) this;
        }

        @Override
        public final SR setPair(BiConsumer<C, Consumer<ItemPair>> consumer) {
            consumer.accept(this.criteria, this::innerAddItemPair);
            return (SR) this;
        }

        @Override
        public final SR set(F field, @Nullable Object value) {
            return this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
        }

        @Override
        public final SR setLiteral(F field, @Nullable Object value) {
            return this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
        }

        @Override
        public final SR setExp(F field, Supplier<? extends Expression> supplier) {
            return this.addFieldPair((FieldMeta<?>) field, supplier.get());
        }

        @Override
        public final SR setExp(F field, Function<C, ? extends Expression> function) {
            return this.addFieldPair((FieldMeta<?>) field, function.apply(this.criteria));
        }

        @Override
        public final SR ifSet(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSet(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableParam(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Supplier<?> supplier) {
            final Object value;
            value = supplier.get();
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Function<C, ?> function) {
            final Object value;
            value = function.apply(this.criteria);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }

        @Override
        public final SR ifSetLiteral(F field, Function<String, ?> function, String keyName) {
            final Object value;
            value = function.apply(keyName);
            if (value != null) {
                this.addFieldPair((FieldMeta<?>) field, SQLs._nullableLiteral(field, value));
            }
            return (SR) this;
        }


        abstract boolean validateField(FieldMeta<?> field);


        final List<ItemPair> endAssignmentSetClause() {
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = Collections.emptyList();
            } else if (!(itemPairList instanceof ArrayList)) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            } else if (itemPairList.size() == 1) {
                itemPairList = Collections.singletonList(itemPairList.get(0));
            } else {
                itemPairList = _CollectionUtils.unmodifiableList(itemPairList);
            }
            this.itemPairList = itemPairList;
            return itemPairList;
        }


        private void innerAddItemPair(final ItemPair itemPair) {
            if (itemPair instanceof SQLs.FieldItemPair) {
                this.validateItemField(((SQLs.FieldItemPair) itemPair).field);
            } else if (!(itemPair instanceof SQLs.RowItemPair)) {
                String m = String.format("Non-Army %s", ItemPair.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            } else if (this.supportRowItem) {
                for (DataField field : ((SQLs.RowItemPair) itemPair).fieldList) {
                    this.validateItemField(field);
                }
            } else {
                throw CriteriaContextStack.criteriaError(this.criteriaContext, "Don't support row item");
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(itemPair);
        }

        private SR addFieldPair(FieldMeta<?> field, @Nullable Expression value) {
            if (!this.validateField(field)) {
                throw notContainField(this.criteriaContext, field);
            }
            if (!(value instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(this.criteriaContext);
            }
            List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null) {
                itemPairList = new ArrayList<>();
                this.itemPairList = itemPairList;
            }
            itemPairList.add(SQLs.itemPair(field, value));
            return (SR) this;
        }

        private void validateItemField(final DataField field) {
            if (!(field instanceof FieldMeta)) {
                String m = String.format("assignment insert syntax support only %s", FieldMeta.class.getName());
                throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
            }
            if (!this.validateField((FieldMeta<?>) field)) {
                throw notContainField(this.criteriaContext, (FieldMeta<?>) field);
            }
        }


    }//AssignmentSetClause


    static abstract class AssignmentInsertClause<C, F extends TableField, SR>
            extends AssignmentSetClause<C, F, SR>
            implements Insert._AssignmentSetClause<C, F, SR>, _Insert._AssignmentInsert, ColumnListClause
            , Statement.StatementMockSpec {

        final boolean migration;

        final NullHandleMode nullHandleMode;

        final boolean supportRowItem;

        final TableMeta<?> table;

        private List<ItemPair> itemPairList;


        AssignmentInsertClause(InsertOptions options, boolean supportRowItem, TableMeta<?> table) {
            super(options.getCriteriaContext(), supportRowItem);
            this.migration = options.isMigration();
            this.nullHandleMode = options.nullHandle();

            this.supportRowItem = supportRowItem;
            this.table = table;
        }

        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return Collections.emptyList();
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return Collections.emptyMap();
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
        public final List<ItemPair> rowPairList() {
            final List<ItemPair> itemPairList = this.itemPairList;
            if (itemPairList == null || itemPairList instanceof ArrayList) {
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return itemPairList;
        }

        @Override
        public void clear() {
            this.itemPairList = null;
        }

        @Override
        public final CriteriaContext getCriteriaContext() {
            return this.criteriaContext;
        }

        @Override
        public final boolean contain(final FieldMeta<?> field) {
            final TableMeta<?> table, fieldTable;
            table = this.table;
            fieldTable = field.tableMeta();
            final boolean match;
            if (fieldTable instanceof ChildTableMeta) {
                match = fieldTable == table;
            } else if (table instanceof ChildTableMeta) {
                match = fieldTable == ((ChildTableMeta<?>) table).parentMeta();
            } else {
                match = fieldTable == table;
            }
            return match;
        }

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _DialectParser d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }


    }//AssignmentInsertClause


    static abstract class InsertStatement<I extends DmlStatement.DmlInsert>
            implements _Insert, Statement.StatementMockSpec, DmlStatement._DmlInsertSpec<I>
            , DmlStatement.DmlInsert {


        private final CriteriaContext criteriaContext;

        private final TableMeta<?> table;

        private final List<FieldMeta<?>> fieldList;

        private final Map<FieldMeta<?>, Boolean> fieldMap;


        private Boolean prepared;

        InsertStatement(_Insert clause) {
            this.criteriaContext = ((CriteriaContextSpec) clause).getCriteriaContext();
            this.table = clause.table();
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
        }


        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            final _DialectParser parser;
            parser = _MockDialects.from(dialect);
            return parser.printStmt(this.mockStmt(parser, visible), none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            return this.mockStmt(_MockDialects.from(dialect), visible);
        }

        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            _Assert.nonPrepared(this.prepared);
            if (this instanceof SubStatement) {
                CriteriaContextStack.pop(this.criteriaContext);
            } else {
                CriteriaContextStack.clearContextStack(this.criteriaContext);
            }
            this.prepared = Boolean.TRUE;
            return (I) this;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }


        private Stmt mockStmt(_DialectParser parser, Visible visible) {
            final Stmt stmt;
            if (this instanceof Insert) {
                stmt = parser.insert((Insert) this, visible);
            } else if (this instanceof ReplaceInsert || this instanceof MergeInsert) {
                stmt = parser.dialectStmt((DialectStatement) this, visible);
            } else {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return stmt;
        }


    }//InsertStatement


    static abstract class ValueSyntaxStatement<I extends DmlStatement.DmlInsert>
            extends InsertStatement<I> implements _Insert._ValuesSyntaxInsert {

        private final boolean migration;
        private final NullHandleMode nullHandleMode;

        private final boolean preferLiteral;

        private final Map<FieldMeta<?>, _Expression> defaultExpMap;

        ValueSyntaxStatement(_ValuesSyntaxInsert clause) {
            super(clause);
            this.migration = clause.isMigration();
            this.nullHandleMode = clause.nullHandle();
            this.preferLiteral = clause.isPreferLiteral();
            this.defaultExpMap = clause.defaultExpMap();
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
        public final Map<FieldMeta<?>, _Expression> defaultExpMap() {
            return this.defaultExpMap;
        }


    }//ValueInsertStatement


    static abstract class QueryInsertStatement<I extends DmlStatement.DmlInsert>
            implements _Insert._QueryInsert, DmlStatement._DmlInsertSpec<I>
            , DmlStatement.DmlInsert, Statement.StatementMockSpec {

        final CriteriaContext criteriaContext;
        final TableMeta<?> table;

        final List<FieldMeta<?>> fieldList;

        final Map<FieldMeta<?>, Boolean> fieldMap;

        final SubQuery subQuery;

        private Boolean prepared;

        QueryInsertStatement(_Insert clause, SubQuery subQuery) {
            this.criteriaContext = ((CriteriaContextSpec) clause).getCriteriaContext();

            this.table = clause.table();
            this.fieldList = clause.fieldList();
            this.fieldMap = clause.fieldMap();
            this.subQuery = subQuery;
        }


        @Override
        public final TableMeta<?> table() {
            return this.table;
        }

        @Override
        public final List<FieldMeta<?>> fieldList() {
            return this.fieldList;
        }

        @Override
        public final Map<FieldMeta<?>, Boolean> fieldMap() {
            return this.fieldMap;
        }

        @Override
        public final SubQuery subQuery() {
            return this.subQuery;
        }

        @SuppressWarnings("unchecked")
        @Override
        public final I asInsert() {
            _Assert.nonPrepared(this.prepared);
            if (this instanceof SubStatement) {
                CriteriaContextStack.pop(this.criteriaContext);
            } else {
                CriteriaContextStack.clearContextStack(this.criteriaContext);
            }
            this.validateStatement();
            this.criteriaContext.clear();
            this.prepared = Boolean.TRUE;
            return (I) this;
        }

        @Override
        public final void prepared() {
            _Assert.prepared(this.prepared);
        }

        @Override
        public final boolean isPrepared() {
            final Boolean prepared = this.prepared;
            return prepared != null && prepared;
        }

        @Override
        public final void clear() {
            _Assert.prepared(this.prepared);
            this.prepared = Boolean.FALSE;
        }

        @Override
        public final String mockAsString(Dialect dialect, Visible visible, boolean none) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            final _DialectParser d;
            d = _MockDialects.from(dialect);
            final Stmt stmt;
            stmt = d.insert((Insert) this, visible);
            return d.printStmt(stmt, none);
        }

        @Override
        public final Stmt mockAsStmt(Dialect dialect, Visible visible) {
            if (!(this instanceof Insert)) {
                //non-primary insert
                throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
            }
            return _MockDialects.from(dialect).insert((Insert) this, visible);
        }

        /**
         * <p>
         * This method is invoked after {@link  CriteriaContextStack#clearContextStack(CriteriaContext)}
         * and {@link  CriteriaContextStack#pop(CriteriaContext)}.
         * </p>
         */
        final void validateStatement() {
            final TableMeta<?> table = this.table;
            if (table instanceof ChildTableMeta) {
                doValidateStatement(((ChildTableMeta<?>) table).parentMeta(), this.fieldList, this.subQuery);
                final _Insert._QueryInsert parentStmt = ((_Insert._ChildQueryInsert) this).parentStmt();
                doValidateStatement(table, parentStmt.fieldList(), parentStmt.subQuery());
            } else {
                doValidateStatement(table, this.fieldList, this.subQuery);
            }

        }

        private static void doValidateStatement(final TableMeta<?> table, final List<FieldMeta<?>> fieldList
                , final @Nullable RowSet rowSet) {
            final int size;
            size = fieldList.size();
            if (size == 0) {
                throw _Exceptions.noFieldsForRowSetInsert(table);
            }
            if (rowSet == null) {
                throw new CriteriaException(String.format("RowSet is null for %s", table));
            }
            final int selectionSize;
            selectionSize = CriteriaUtils.selectionCount(rowSet);
            if (selectionSize != size) {
                throw _Exceptions.rowSetSelectionAndFieldSizeNotMatch(selectionSize, size, table);
            }
        }


    }//RowSetInsertStatement


    static CriteriaException notContainField(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("insert field list don't contain %s", field);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException duplicationValuePair(CriteriaContext criteriaContext, FieldMeta<?> field) {
        String m = String.format("duplication value of %s at same row.", field);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    static CriteriaException subQueryIsNull(CriteriaContext criteriaContext) {
        return CriteriaContextStack.criteriaError(criteriaContext, "sub query must be non-null");
    }

    static CriteriaException noColumnList(CriteriaContext criteriaContext, TableMeta<?> table) {
        String m = String.format("You must specified column list of %s for row set insert.", table);
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }

    private static CriteriaException nonDomainInstance(CriteriaContext criteriaContext, @Nullable Object domain
            , TableMeta<?> table) {
        String m;
        m = String.format("%s isn't %s instance.", _ClassUtils.safeClassName(domain), table.javaType().getName());
        return CriteriaContextStack.criteriaError(criteriaContext, m);
    }


}
