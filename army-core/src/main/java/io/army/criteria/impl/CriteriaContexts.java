package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Constant;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;

abstract class CriteriaContexts {


    static CriteriaContext queryContext(@Nullable Object criteria) {
        return new SimpleQueryContext(criteria);
    }


    private static CriteriaException dontSupportRefSelection() {
        return new CriteriaException("Non union query couldn't reference selection of query.");
    }

    private static CriteriaException invalidRef(String subQueryAlias, String fieldName) {
        String m = String.format("ref of %s.%s is invalid.", subQueryAlias, fieldName);
        return new CriteriaException(m);
    }

    private static abstract class AbstractContext implements CriteriaContext {

        final Object criteria;


        private AbstractContext(@Nullable Object criteria) {
            this.criteria = criteria;
        }


        @SuppressWarnings("unchecked")
        @Override
        public final <C> C criteria() {
            try {
                return (C) this.criteria;
            } catch (ClassCastException e) {
                String m = String.format("Criteria cast failure,please check %s method parameter type."
                        , Function.class.getName());
                throw new CriteriaException(m, e);
            }
        }


    }

    private static abstract class JoinableContext extends AbstractContext {

        private List<_TableBlock> tableBlockList = new ArrayList<>();

        Map<String, _TableBlock> aliasToBlock = new HashMap<>();

        private Map<String, Map<FieldMeta<?, ?>, QualifiedField<?, ?>>> aliasToField;

        Map<String, Map<String, DerivedFieldImpl<?>>> aliasToDerivedField;

        private Map<String, Map<String, DerivedSelection<?>>> aliasToSelection;


        private JoinableContext(@Nullable Object criteria) {
            super(criteria);
        }


        @Override
        public final void onAddBlock(final _TableBlock block) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock.size() == 0) {
                throw _Exceptions.castCriteriaApi();
            }
            if (aliasToBlock.putIfAbsent(block.alias(), block) != null) {
                throw _Exceptions.tableAliasDuplication(block.alias());
            }
            this.tableBlockList.add(block);
            this.doOnAddBlock(block);
        }

        @Override
        public final void onFirstBlock(final _TableBlock block) {
            if (block.jointType() != _JoinType.NONE || block.predicates().size() > 0) {
                throw new IllegalArgumentException("Not first block");
            }
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            if (aliasToBlock.size() > 0) {
                throw _Exceptions.castCriteriaApi();
            }
            if (aliasToBlock.putIfAbsent(block.alias(), block) != null) {
                throw _Exceptions.tableAliasDuplication(block.alias());
            }
            this.tableBlockList.add(block);
            this.doOnAddBlock(block);
        }

        @Override
        public final _TableBlock firstBlock() {
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            if (tableBlockList.size() != 1) {
                throw _Exceptions.castCriteriaApi();
            }
            return tableBlockList.get(0);
        }


        @SuppressWarnings("unchecked")
        @Override
        public final <T extends IDomain, F> QualifiedField<T, F> qualifiedField(String tableAlias, FieldMeta<T, F> field) {
            Map<String, Map<FieldMeta<?, ?>, QualifiedField<?, ?>>> aliasToField = this.aliasToField;
            if (aliasToField == null) {
                aliasToField = new HashMap<>();
                this.aliasToField = aliasToField;
            }
            final Map<FieldMeta<?, ?>, QualifiedField<?, ?>> fieldMap;
            fieldMap = aliasToField.computeIfAbsent(tableAlias, k -> new HashMap<>());

            final QualifiedField<?, ?> qualifiedField;
            qualifiedField = fieldMap.computeIfAbsent(field, k -> new QualifiedFieldImpl<>(tableAlias, k));
            return (QualifiedField<T, F>) qualifiedField;
        }


        @Override
        public <E> DerivedField<E> ref(final String subQueryAlias, final String fieldName) {
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            final _TableBlock block = aliasToBlock.get(subQueryAlias);
            final TablePart subQuery;
            final DerivedField<E> field;
            if (block == null) {
                field = getField(subQueryAlias, fieldName, true);
                assert field != null;
            } else if (!((subQuery = block.table()) instanceof SubQuery)) {
                //TODO handle MySQL TablePartGroup
                String m = String.format("%s isn't alias of %s ", subQueryAlias, SubQuery.class.getName());
                throw new CriteriaException(m);
            } else {
                final DerivedField<E> temp;
                temp = getField(subQueryAlias, fieldName, false);
                if (temp == null) {
                    field = getSelection((SubQuery) subQuery, subQueryAlias, fieldName);
                } else {
                    field = temp;
                }
            }
            return field;
        }

        @Override
        public <E> Expression<E> ref(String selectionAlias) {
            throw dontSupportRefSelection();
        }


        @Override
        public final List<_TableBlock> clear() {
            final List<_TableBlock> tableBlockList = this.tableBlockList;
            if (tableBlockList == null) {
                throw new IllegalStateException("duplication clear");
            }
            this.tableBlockList = null;
            final Map<String, _TableBlock> aliasToBlock = this.aliasToBlock;
            this.aliasToBlock = null;
            aliasToBlock.clear();

            final Map<String, Map<FieldMeta<?, ?>, QualifiedField<?, ?>>> aliasToField = this.aliasToField;
            if (aliasToField != null) {
                for (Map<FieldMeta<?, ?>, QualifiedField<?, ?>> map : aliasToField.values()) {
                    map.clear();
                }
                this.aliasToField = null;
                aliasToField.clear();
            }
            this.onClear();
            return CollectionUtils.unmodifiableList(tableBlockList);
        }

        abstract void doOnAddBlock(_TableBlock block);

        abstract void onClear();


        @Nullable
        @SuppressWarnings("unchecked")
        private <E> DerivedField<E> getField(final String subQueryAlias, final String fieldName, final boolean create) {
            Map<String, Map<String, DerivedFieldImpl<?>>> aliasToDerivedField = this.aliasToDerivedField;
            if (aliasToDerivedField == null) {
                aliasToDerivedField = new HashMap<>();
                this.aliasToDerivedField = aliasToDerivedField;
            }
            final Map<String, DerivedFieldImpl<?>> fieldMap;
            final DerivedField<?> field;
            if (create) {
                fieldMap = aliasToDerivedField.computeIfAbsent(subQueryAlias, k -> new HashMap<>());
                field = fieldMap.computeIfAbsent(fieldName, k -> new DerivedFieldImpl<>(subQueryAlias, fieldName));
            } else {
                fieldMap = aliasToDerivedField.get(subQueryAlias);
                if (fieldMap == null) {
                    field = null;
                } else {
                    field = fieldMap.get(fieldName);
                }
            }
            return (DerivedField<E>) field;
        }

        @SuppressWarnings("unchecked")
        private <E> DerivedSelection<E> getSelection(final SubQuery subQuery, final String subQueryAlias
                , final String fieldName) {
            Map<String, Map<String, DerivedSelection<?>>> aliasToSelection = this.aliasToSelection;
            if (aliasToSelection == null) {
                aliasToSelection = new HashMap<>();
                this.aliasToSelection = aliasToSelection;
            }
            final Map<String, DerivedSelection<?>> fieldMap;
            fieldMap = aliasToSelection.computeIfAbsent(subQueryAlias, k -> new HashMap<>());

            final DerivedSelection<?> field;
            field = fieldMap.computeIfAbsent(fieldName
                    , k -> {
                        final Selection selection;
                        selection = subQuery.selection(fieldName);
                        if (selection == null) {
                            throw invalidRef(subQueryAlias, fieldName);
                        }
                        return new DerivedSelection<>(subQueryAlias, selection);
                    });
            return (DerivedSelection<E>) field;
        }


    }


    private static final class SimpleQueryContext extends JoinableContext {


        private List<DerivedSelectionGroup> groupList;


        private SimpleQueryContext(@Nullable Object criteria) {
            super(criteria);
        }

        @Override
        public void selectList(List<SelectPart> selectPartList) {
            if (this.groupList != null || this.aliasToBlock.size() != 0) {
                throw new IllegalStateException("error");
            }
            List<DerivedSelectionGroup> selectionGroupList = null;
            for (SelectPart selectPart : selectPartList) {
                if (!(selectPart instanceof DerivedSelectionGroup)) {
                    continue;
                }
                if (selectionGroupList == null) {
                    selectionGroupList = new LinkedList<>();
                }
                selectionGroupList.add((DerivedSelectionGroup) selectPart);
            }
            if (selectionGroupList == null) {
                this.groupList = Collections.emptyList();
            } else {
                this.groupList = selectionGroupList;
            }
        }

        @Override
        void doOnAddBlock(final _TableBlock block) {
            //TODO handle MySQL TablePartGroup
            final TablePart tablePart;
            tablePart = block.table();
            if (!(tablePart instanceof SubQuery)) {
                return;
            }
            final SubQuery subQuery = (SubQuery) tablePart;
            final String queryAlias = block.alias();

            final Map<String, Map<String, DerivedFieldImpl<?>>> aliasToDerivedField = this.aliasToDerivedField;
            if (aliasToDerivedField != null) {
                final Map<String, DerivedFieldImpl<?>> fieldMap;
                fieldMap = aliasToDerivedField.get(queryAlias);
                if (fieldMap != null) {
                    for (DerivedFieldImpl<?> field : fieldMap.values()) {
                        Selection selection;
                        selection = subQuery.selection(field.fieldName);
                        if (selection == null) {
                            throw invalidRef(queryAlias, field.fieldName);
                        }
                        field.paramMeta.actual = selection.paramMeta();
                    }
                }

            }

            final List<DerivedSelectionGroup> groupList = this.groupList;
            assert groupList != null;
            if (groupList.size() == 0) {
                return;
            }
            final Iterator<DerivedSelectionGroup> iterator = groupList.listIterator();
            while (iterator.hasNext()) {
                final DerivedSelectionGroup group = iterator.next();
                if (queryAlias.equals(group.tableAlias())) {
                    group.finish(subQuery, queryAlias);
                    iterator.remove();
                }
            }

        }

        @Override
        void onClear() {
            final List<DerivedSelectionGroup> groupList = this.groupList;
            if (CollectionUtils.isEmpty(groupList)) {
                return;
            }
            for (DerivedSelectionGroup group : groupList) {
                String m = String.format("DerivedGroup[%s] is invalid.", group.tableAlias());
                throw new CriteriaException(m);
            }

        }

    }//SimpleQueryContext

    private static final class DerivedSelection<E> extends OperationExpression<E> implements DerivedField<E> {

        private final String tableName;

        private final Selection selection;

        private DerivedSelection(String tableName, Selection selection) {
            this.tableName = tableName;
            this.selection = selection;
        }

        @Override
        public String tableName() {
            return this.tableName;
        }

        @Override
        public String alias() {
            return this.selection.alias();
        }

        @Override
        public boolean nullable() {
            return this.selection.nullable();
        }

        @Override
        public ParamMeta paramMeta() {
            return this.selection.paramMeta();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(dialect.quoteIfNeed(this.tableName))
                    .append(Constant.POINT)
                    .append(dialect.quoteIfNeed(this.selection.alias()));

        }

        @Override
        public String toString() {
            return String.format(" %s.%s", this.tableName, this.selection.alias());
        }

    }//DerivedSelection


    private static final class DerivedFieldImpl<E> extends OperationExpression<E> implements DerivedField<E> {

        final String tableName;

        final String fieldName;

        final DelayParamMeta paramMeta;

        private DerivedFieldImpl(String tableName, String fieldName) {
            this.tableName = tableName;
            this.fieldName = fieldName;
            this.paramMeta = new DelayParamMeta();
        }

        @Override
        public String tableName() {
            return this.tableName;
        }

        @Override
        public String alias() {
            return this.fieldName;
        }

        @Override
        public boolean nullable() {
            return false;
        }

        @Override
        public ParamMeta paramMeta() {
            return this.paramMeta;
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final _Dialect dialect = context.dialect();
            context.sqlBuilder()
                    .append(Constant.SPACE)
                    .append(dialect.quoteIfNeed(this.tableName))
                    .append(Constant.POINT)
                    .append(dialect.quoteIfNeed(this.fieldName));
        }


        @Override
        public String toString() {
            return String.format(" %s.%s", this.tableName, this.fieldName);
        }


    }//DerivedFieldImpl


    private static final class DelayParamMeta implements ParamMeta {

        private ParamMeta actual;

        @Override
        public MappingType mappingType() {
            final ParamMeta actual = this.actual;
            if (actual == null) {
                throw new IllegalStateException(String.format("No actual %s", ParamMeta.class.getName()));
            }
            return actual.mappingType();
        }

    }


}