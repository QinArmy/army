/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.impl.inner.postgre._PostgreCte;
import io.army.criteria.impl.inner.postgre._PostgreUpdate;
import io.army.criteria.postgre.FuncColumnDefCommaClause;
import io.army.criteria.postgre.PostgreDelete;
import io.army.criteria.postgre.PostgreStatement;
import io.army.criteria.postgre.PostgreUpdate;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.postgre.PostgreDialect;
import io.army.mapping.MappingType;
import io.army.meta.ChildTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.PrimaryFieldMeta;
import io.army.meta.TableMeta;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }

    /**
     * reference last dialect
     */
    static final PostgreDialect DIALECT = PostgreDialect.POSTGRE16;


    static void validateDmlInWithClause(final List<_Cte> cteList, final @Nullable PostgreStatement mainStmt) {
        final int cteSize = cteList.size(), lastCteIndex = cteSize - 1;

        _PostgreCte cte;
        SubStatement subStatement;
        List<_Cte> subCteList;

        TableMeta<?> targetTable;
        ParentTableMeta<?> parent;
        ChildTableMeta<?> child;
        List<_TabularBlock> blockList;

        _TabularBlock block;
        String childAlias;
        List<_Predicate> predicateList;
        TableField parentId;

        DerivedField refField;
        for (int cteIndex = 0, predicateSize, tabularItemSize; cteIndex < cteSize; cteIndex++) {
            cte = (_PostgreCte) cteList.get(cteIndex);
            subStatement = cte.subStatement();

            subCteList = ((_Statement._WithClauseSpec) subStatement).cteList();
            if (subCteList.size() > 0) {
                validateDmlInWithClause(subCteList, (PostgreStatement) subStatement);
            }

            if (!(subStatement instanceof _SingleDml)) {
                continue;
            }
            if (!((targetTable = ((_SingleDml) subStatement).table()) instanceof ChildTableMeta)) {
                continue;
            }

            child = (ChildTableMeta<?>) targetTable;
            childAlias = ((_SingleDml) subStatement).tableAlias();
            blockList = ((_Statement._JoinableStatement) subStatement).tableBlockList();

            if ((tabularItemSize = blockList.size()) == 0) {
                throw cteChildNoJoinParent(cte.name(), child, childAlias);
            }

            predicateList = ((_SingleDml) subStatement).wherePredicateList();
            predicateSize = predicateList.size();
            parent = ((ChildTableMeta<?>) targetTable).parentMeta();

            if (tabularItemSize == 1
                    && subStatement instanceof PostgreUpdate
                    && blockList.get(0).tableItem() instanceof _Cte
                    && predicateSize == 1) { // join parent cte

                refField = idFieldEqualCteIdField(predicateList.get(0), child.id());
                if (refField == null || cteIndex == 0) {
                    throw cteChildNoJoinParent(cte.name(), child, childAlias);
                }
                validateParentCte(child, childAlias, blockList.get(0), cte.name(), refField);
                continue;
            }


            parentId = null;
            for (int predicateIndex = 0; predicateIndex < predicateSize; predicateIndex++) {
                parentId = predicateList.get(predicateIndex).findParentId(child, childAlias);
                if (parentId != null) {
                    break;
                }
            }


            if (parentId == null) {
                final String idFieldName = child.id().fieldName();
                String m = String.format("Cte[%s] WHERE no the predicate %s.%s = %s.%s", cte.name(), child, idFieldName,
                        parent, idFieldName);
                throw ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
            }

            refField = null; // clear
            for (int blockIndex = 0; blockIndex < tabularItemSize; blockIndex++) {
                block = blockList.get(blockIndex);
                if (block.tableItem() != parent) {
                    continue;
                }

                if (parentId instanceof QualifiedField
                        && !((QualifiedField<?>) parentId).tableAlias().equals(block.alias())) {
                    continue;
                }

                if (cteIndex < lastCteIndex) {
                    refField = validateParentTable(child, cte, cteIndex, cteList);
                }
                if (refField == null && mainStmt instanceof _SingleDml && ((_SingleDml) mainStmt).table() == parent) {
                    refField = validateParentTableFromMainStmt(parent.id(), cte.name(), (_SingleDml) mainStmt);
                }
                if (refField == null || !(subStatement instanceof _ReturningDml)) {
                    throw cteChildNoJoinParent(cte.name(), child, childAlias);
                }

                if (!validatePrimaryIdSelection(child, childAlias, (_Statement._ReturningListSpec) subStatement, refField)) {
                    throw cteChildNoJoinParent(cte.name(), child, childAlias);
                }

                break;

            } // block loop for

            if (refField == null) {
                throw cteChildNoJoinParent(cte.name(), child, childAlias);
            }


        } // for loop

        if (mainStmt instanceof PrimaryStatement
                && mainStmt instanceof _PostgreUpdate
                && (targetTable = ((_SingleDml) mainStmt).table()) instanceof ChildTableMeta) { // here never DELETE statement

            blockList = ((_Statement._JoinableStatement) mainStmt).tableBlockList();
            child = (ChildTableMeta<?>) targetTable;
            childAlias = ((_SingleDml) mainStmt).tableAlias();
            if (blockList.size() != 1 || !((blockList.get(0)).tableItem() instanceof _Cte)) {
                throw cteChildNoJoinParent(null, child, childAlias);
            }
            predicateList = ((_SingleDml) mainStmt).wherePredicateList();
            if (predicateList.size() != 1 || cteSize == 0) {
                throw cteChildNoJoinParent(null, child, childAlias);
            }
            refField = idFieldEqualCteIdField(predicateList.get(0), child.id());

            if (refField == null) {
                throw cteChildNoJoinParent(null, child, childAlias);
            }

            validateParentCte(child, childAlias, blockList.get(0), null, refField);
        }

    }


    static boolean isUnionQuery(final SubQuery query) {
        _RowSet rowSet = (_RowSet) query;
        while (rowSet instanceof _ParensRowSet) {
            rowSet = ((_ParensRowSet) rowSet).innerRowSet();
        }
        return rowSet instanceof SimpleQueries.UnionSubQuery;
    }


    static <R extends Item> PostgreStatement._FuncColumnDefinitionParensClause<R> undoneFunc(
            final UndoneFunction func, final Function<DoneFunc, R> function) {
        return c -> {
            final FuncColumnDefinitionClause clause;
            clause = new FuncColumnDefinitionClause();

            c.accept(clause);

            clause.endClause(); // end clause
            return function.apply(new DoneFunc(func, clause.fieldList, clause.fieldMap));
        };
    }

    static <R> Function<Consumer<PostgreStatement._FuncColumnDefinitionSpaceClause>, R> rowsFromUndoneFunc(
            final UndoneFunction func, final Function<DoneFunc, R> function) {
        return c -> {
            final FuncColumnDefinitionClause clause;
            clause = new FuncColumnDefinitionClause();

            c.accept(clause);

            clause.endClause(); // end clause
            return function.apply(new DoneFunc(func, clause.fieldList, clause.fieldMap));
        };
    }


    /*-------------------below private methods-------------------*/

    /**
     * @see #validateDmlInWithClause(List, PostgreStatement)
     */
    private static CriteriaException cteChildNoJoinParent(@Nullable String cteName, ChildTableMeta<?> child, String tableAlias) {
        if (cteName == null) {
            cteName = "";
        } else {
            cteName = "Cte " + cteName + " ";
        }
        String m = String.format("%s%s [alias : %s] is child ,but don't join parent or cte of parent",
                cteName, child, tableAlias);
        return ContextStack.clearStackAnd(IllegalOneStmtModeException::new, m);
    }

    /**
     * @see #validateDmlInWithClause(List, PostgreStatement)
     */
    private static void validateParentCte(final ChildTableMeta<?> child, String childAlias, final _TabularBlock block,
                                          final @Nullable String childCteName, final DerivedField refField) {
        final ParentTableMeta<?> parent = child.parentMeta();

        final _Cte parentCte = (_Cte) block.tableItem();
        final SubStatement subStatement;
        subStatement = parentCte.subStatement();

        if (!(subStatement instanceof _PostgreUpdate)) {
            throw cteChildNoJoinParent(childCteName, child, childAlias);
        }

        if (((_PostgreUpdate) subStatement).table() != parent) {
            throw cteChildNoJoinParent(childCteName, child, childAlias);
        }

        if (!(subStatement instanceof _ReturningDml)) {
            throw cteChildNoJoinParent(childCteName, child, childAlias);
        }

        validatePrimaryIdSelection(child, childAlias, ((_Statement._ReturningListSpec) subStatement), refField);

    }

    /**
     * @param currentCteIndex -1 or positive
     * @see #validateDmlInWithClause(List, PostgreStatement)
     */
    @Nullable
    private static DerivedField validateParentTable(final ChildTableMeta<?> child, final _Cte childCte,
                                                    final int currentCteIndex, final List<_Cte> cteList) {
        final int cteSize = cteList.size();
        final _SingleDml childSubStatement = (_SingleDml) childCte.subStatement();
        final boolean childIsUpdate = childSubStatement instanceof PostgreUpdate;
        final ParentTableMeta<?> parent = child.parentMeta();

        final PrimaryFieldMeta<?> parentId = parent.id();

        _Cte parentCte, refCte;
        SubStatement subStatement;
        List<_TabularBlock> blockList;
        _TabularBlock block;

        List<_Predicate> predicateList;
        DerivedField derivedField = null;
        String childCteAlias;
        for (int i = currentCteIndex + 1; i < cteSize; i++) {
            parentCte = cteList.get(i);
            subStatement = parentCte.subStatement();

            if (childIsUpdate) {
                if (!(subStatement instanceof PostgreUpdate)) {
                    continue;
                }
            } else if (!(subStatement instanceof PostgreDelete)) {
                continue;
            }

            if (((_SingleDml) subStatement).table() != parent) {
                continue;
            }

            blockList = ((_Statement._JoinableStatement) subStatement).tableBlockList();

            if (blockList.size() != 1) {
                continue;
            }

            block = blockList.get(0);
            if (!(block.tableItem() instanceof _Cte)) {
                continue;
            }

            refCte = (_Cte) block.tableItem();
            if (!refCte.name().equals(childCte.name())) {
                continue;
            }

            predicateList = ((_SingleDml) subStatement).wherePredicateList();

            if (predicateList.size() != 1) {
                continue;
            }

            derivedField = idFieldEqualCteIdField(predicateList.get(0), parentId);
            if (derivedField == null) {
                continue;
            }

            childCteAlias = block.alias();
            if (!_StringUtils.hasText(childCteAlias)) {
                childCteAlias = refCte.name();
            }
            if (childCteAlias.equals(derivedField.tableAlias())) {
                break;
            }

            derivedField = null;

        } // loop for

        return derivedField;
    }


    /**
     * @return false : error
     * @see #validateDmlInWithClause(List, PostgreStatement)
     */
    private static boolean validatePrimaryIdSelection(final ChildTableMeta<?> child, final String alias,
                                                      final _Statement._ReturningListSpec spec,
                                                      final DerivedField refField) {

        final List<? extends _SelectItem> selectItemList = spec.returningList();
        final int itemSize = selectItemList.size();
        final String label = refField.label();

        _SelectItem selectItem;
        Selection selection = null;

        List<? extends Selection> selectionList;

        outerLoop:
        for (int i = 0, groupSize; i < itemSize; i++) {
            selectItem = selectItemList.get(i);
            selection = null;
            if (selectItem instanceof Selection) {
                selection = (Selection) selectItem;
                if (label.equals(selection.label())) {
                    break;
                }
                continue;
            }

            selectionList = ((_SelectionGroup) selectItem).selectionList();
            groupSize = selectionList.size();
            for (int j = 0; j < groupSize; j++) {
                selection = selectionList.get(j);
                if (label.equals(selection.label())) {
                    break outerLoop;
                }
            }

        } // loop for

        if (selection == null) {
            // no bug,never here
            throw CriteriaUtils.unknownSelection(label);
        }

        final PrimaryFieldMeta<?> idMeta = child.id();
        final TableField tableField;
        return selection == idMeta
                || (tableField = ((_Selection) selection).tableField()) == idMeta
                || (tableField instanceof QualifiedField
                && tableField.fieldMeta() == idMeta
                && alias.equals(((QualifiedField<?>) tableField).tableAlias()));
    }

    /**
     * @see #validateDmlInWithClause(List, PostgreStatement)
     */
    @Nullable
    private static DerivedField validateParentTableFromMainStmt(final PrimaryFieldMeta<?> idField, final String refCteName,
                                                                final _SingleDml mainStmt) {
        final List<_TabularBlock> blockList = ((_Statement._JoinableStatement) mainStmt).tableBlockList();

        if (blockList.size() != 1 || !(blockList.get(0).tableItem() instanceof _Cte)) {
            return null;
        }

        final List<_Predicate> predicateList = mainStmt.wherePredicateList();

        if (predicateList.size() != 1) {
            return null;
        }

        final _TabularBlock cteBlock = blockList.get(0);
        final _Cte refCte = (_Cte) cteBlock.tableItem();

        if (!refCte.name().equals(refCteName)) {
            return null;
        }

        final DerivedField field;
        field = idFieldEqualCteIdField(predicateList.get(0), idField);
        if (field == null) {
            return null;
        }


        String cteAlias;
        cteAlias = cteBlock.alias();
        if (!_StringUtils.hasText(cteAlias)) {
            cteAlias = refCte.name();
        }

        if (!field.tableAlias().equals(cteAlias)) {
            return null;
        }
        return field;
    }


    /**
     * @see #validateParentTableFromMainStmt(PrimaryFieldMeta, String, _SingleDml)
     * @see #validateParentTable(ChildTableMeta, _Cte, int, List)
     * @see #validateParentCte(ChildTableMeta, String, _TabularBlock, String, DerivedField)
     */
    @Nullable
    private static DerivedField idFieldEqualCteIdField(final _Predicate predicate, final PrimaryFieldMeta<?> idField) {

        if (!(predicate instanceof Expressions.DualPredicate)) {
            return null;
        }

        final Expressions.DualPredicate dualPredicate = (Expressions.DualPredicate) predicate;

        boolean leftId, rightId;
        leftId = rightId = false;
        if (dualPredicate.left == idField) {
            leftId = true;
        } else if (dualPredicate.left instanceof QualifiedField) {
            leftId = ((QualifiedField<?>) dualPredicate.left).fieldMeta() == idField;
        } else if (dualPredicate.right == idField) {
            rightId = true;
        } else if (dualPredicate.right instanceof QualifiedField) {
            rightId = ((QualifiedField<?>) dualPredicate.right).fieldMeta() == idField;
        }

        final DerivedField derivedField;
        if (leftId) {
            if (dualPredicate.right instanceof DerivedField) {
                derivedField = (DerivedField) dualPredicate.right;
            } else {
                derivedField = null;
            }
        } else if (rightId) {
            if (dualPredicate.left instanceof DerivedField) {
                derivedField = (DerivedField) dualPredicate.left;
            } else {
                derivedField = null;
            }

        } else {
            derivedField = null;
        }
        return derivedField;
    }

    static CriteriaException nonRecursiveWithClause() {
        return ContextStack.clearStackAndCriteriaError("Non-recursive WITH clause don't support SEARCH or CYCLE clause");
    }



    /*-------------------below inner class -------------------*/

    private static final class FuncColumnDefinitionClause
            implements PostgreStatement._FuncColumnDefinitionSpaceClause,
            FuncColumnDefCommaClause {

        private List<_FunctionField> fieldList = _Collections.arrayList();

        private Map<String, _FunctionField> fieldMap = _Collections.hashMap();

        private Boolean state;

        private FuncColumnDefinitionClause() {
        }

        @Override
        public FuncColumnDefCommaClause space(String name, MappingType type) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.comma(name, type);
        }

        @Override
        public FuncColumnDefCommaClause comma(final @Nullable String name,
                                              @Nullable final MappingType type) {
            final List<_FunctionField> fieldList = this.fieldList;
            if (this.state != Boolean.TRUE || !(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (name == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (type == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(name)) {
                throw CriteriaUtils.funcFieldNameNoText();
            }
            final Map<String, _FunctionField> fieldMap = this.fieldMap;

            final _FunctionField field;
            field = DialectFunctionUtils.funcField(name, type);
            if (fieldMap.putIfAbsent(name, field) != null) {
                throw CriteriaUtils.funcFieldDuplication(name);
            }
            fieldList.add(field);
            return this;
        }


        void endClause() {
            final List<_FunctionField> fieldList = this.fieldList;
            if (!(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (fieldList.size() == 0) {
                throw ContextStack.clearStackAndCriteriaError("you don't add any column definition.");
            }
            this.fieldList = _Collections.unmodifiableList(fieldList);
            this.fieldMap = _Collections.unmodifiableMap(this.fieldMap);
            this.state = Boolean.FALSE;
        }

    }//FuncColumnDefinitionClause


    static final class DoneFunc implements ArmySQLFunction {

        final UndoneFunction funcItem;

        final List<_FunctionField> fieldList;

        final Map<String, _FunctionField> fieldMap;

        /**
         * @param fieldList unmodified list
         * @param fieldMap  unmodified map
         */
        private DoneFunc(UndoneFunction funcItem, List<_FunctionField> fieldList,
                         Map<String, _FunctionField> fieldMap) {
            this.funcItem = funcItem;
            this.fieldList = fieldList;
            this.fieldMap = fieldMap;

        }

        @Override
        public String name() {
            return this.funcItem.name();
        }

        /**
         * this method for ROWS FROM( ... ) syntax.
         */
        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            ((ArmySQLFunction) this.funcItem).appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_AS)
                    .append(_Constant.LEFT_PAREN);
            CriteriaUtils.appendSelfDescribedList(this.fieldList, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.funcItem)
                    .append(_Constant.SPACE_AS)
                    .append(_Constant.LEFT_PAREN);

            CriteriaUtils.selfDescribedListToString(this.fieldList, builder);

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//PostgreDoneFunc


}
