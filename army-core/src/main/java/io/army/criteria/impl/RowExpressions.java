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
import io.army.dialect.Database;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping.NoCastIntegerType;
import io.army.mapping.NoCastTextType;
import io.army.mapping._MappingFactory;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the methods that create {@link io.army.criteria.RowExpression}.
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 0.6.0
 */
abstract class RowExpressions {

    private RowExpressions() {
        throw new UnsupportedOperationException();
    }

    static RowExpression emptyRow() {
        return new ImmutableRowConstructor(_Collections.emptyList(), 0);
    }

    static RowExpression row(final @Nullable SubQuery query) {
        if (query == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new ImmutableRowConstructor(_Collections.singletonList(query),
                ((ArmySubQuery) query).refAllSelection().size()
        );
    }

    static RowExpression row(final Object element) {
        final Object actualElement;
        if (element instanceof Supplier) {
            actualElement = ((Supplier<?>) element).get();
        } else {
            actualElement = element;
        }
        return new ImmutableRowConstructor(_Collections.singletonList(actualElement),
                rowElementColumnSize(actualElement)
        );
    }

    static RowExpression row(final Object element1, final Object element2) {
        final int columnSize;
        columnSize = rowElementColumnSize(element1) + rowElementColumnSize(element2);
        return new ImmutableRowConstructor(ArrayUtils.of(element1, element2), columnSize);
    }

    static RowExpression row(final Object element1, final Object element2, final Object element3, final Object... rest) {

        int columnSize = 0;

        columnSize += rowElementColumnSize(element1);
        columnSize += rowElementColumnSize(element2);
        columnSize += rowElementColumnSize(element3);

        final List<Object> elementList = _Collections.arrayList(3 + rest.length);
        elementList.add(element1);
        elementList.add(element2);
        elementList.add(element3);

        Collections.addAll(elementList, rest);
        columnSize += rest.length;
        return new ImmutableRowConstructor(elementList, columnSize);
    }

    static RowExpression row(Consumer<Consumer<Object>> consumer) {
        final List<Object> elementList = _Collections.arrayList();
        final int[] columnSizeHolder = new int[]{0};
        consumer.accept(e -> {
            columnSizeHolder[0] += rowElementColumnSize(e);
            elementList.add(e);
        });

        final int columnSize = columnSizeHolder[0];
        return new ImmutableRowConstructor(elementList, columnSize);
    }


    static void validateColumnSize(final SQLColumnSet left, final SQLColumnSet right) {
        if (right instanceof SubQuery) {
            Expressions.validateSubQueryContext((SubQuery) right);
        }
        doValidateColumnSize(left, right);
    }

    /*-------------------below private method-------------------*/

    private static int rowElementColumnSize(final Object element) {
        final int columnSize;
        if (!(element instanceof RowElement) || element instanceof SQLExpression) {
            columnSize = 1;
        } else if (element instanceof SubQuery) {
            columnSize = ((ArmySubQuery) element).refAllSelection().size();
        } else if (element instanceof SelectionGroups.RowElementGroup) {
            columnSize = ((SelectionGroups.RowElementGroup) element).selectionList().size();
        } else {
            throw ContextStack.clearStackAnd(_Exceptions::unknownRowElement, element);
        }
        return columnSize;
    }


    private static void doValidateColumnSize(final SQLColumnSet left, final SQLColumnSet right) {
        final int leftSize, rightSize;

        if (left instanceof RowExpression) {
            leftSize = ((ArmyRowExpression) left).columnSize();
        } else if (left instanceof SubQuery) {
            leftSize = ((ArmySubQuery) left).refAllSelection().size();
        } else {
            // no bug,never here
            throw new IllegalStateException();
        }

        if (right instanceof RowExpression) {
            rightSize = ((ArmyRowExpression) right).columnSize();
        } else if (right instanceof SubQuery) {
            rightSize = ((ArmySubQuery) right).refAllSelection().size();
        } else {
            // no bug,never here
            throw new IllegalStateException();
        }

        if (leftSize != rightSize) {
            String m;
            m = String.format("left operand %s column size[%s] and right operand %s column size[%s] not match",
                    left, leftSize, right, rightSize);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
    }

    private static abstract class RowConstructorExpression extends OperationRowExpression {

        final List<Object> elementList;


        private RowConstructorExpression(final List<Object> elementList) {
            this.elementList = elementList;
        }


        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            final List<Object> elementList = this.elementList;
            final int elementItemSize;
            elementItemSize = elementList.size();
            final Database database;
            database = context.database();
            if (elementItemSize == 0 && database == Database.MySQL) {
                String m = String.format("error ,%s don't support empty row", database);
                throw new CriteriaException(m);
            }


            Object element;
            MappingType type;
            final int startSqlIndex = sqlBuilder.length();

            sqlBuilder.append(_Constant.SPACE)
                    .append("ROW");
            int outputColumnCount = 0;
            sqlBuilder.append(_Constant.LEFT_PAREN);
            for (int i = 0; i < elementItemSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                element = elementList.get(i);
                if (element == null) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                    outputColumnCount++;
                } else if (!(element instanceof RowElement)) {
                    if (element instanceof Integer) {
                        type = NoCastIntegerType.INSTANCE;
                    } else if (element instanceof String) {
                        type = NoCastTextType.INSTANCE;
                    } else if ((type = _MappingFactory.getDefaultIfMatch(element.getClass())) == null) {
                        throw _Exceptions.notFoundMappingType(element);
                    }
                    context.appendLiteral(type, element);

                    outputColumnCount++;
                } else if (element instanceof SubQuery) {
                    context.appendSubQuery((SubQuery) element);
                    outputColumnCount += ((ArmySubQuery) element).refAllSelection().size();
                } else if (element instanceof ArmySQLExpression) {
                    ((ArmySQLExpression) element).appendSql(sqlBuilder, context);
                    outputColumnCount++;
                } else if (element instanceof SelectionGroups.RowElementGroup) {
                    ((SelectionGroups.RowElementGroup) element).appendRowElement(sqlBuilder, context);
                    outputColumnCount += ((SelectionGroups.RowElementGroup) element).selectionList().size();
                } else {
                    throw _Exceptions.unknownRowElement(element);
                }

            }//for

            if (outputColumnCount == 1
                    && database == Database.MySQL
                    && !(elementList.get(0) instanceof RowElement)) {
                // MySQL don't support one column row
                final int from = startSqlIndex + 1, end = startSqlIndex + 4;
                assert sqlBuilder.charAt(startSqlIndex) == _Constant.SPACE;
                assert sqlBuilder.charAt(end) == _Constant.LEFT_PAREN;
                assert sqlBuilder.substring(from, end).equals("ROW");
                sqlBuilder.delete(from, end);
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public final String toString() {
            final List<Object> elementList = this.elementList;
            final int elementItemSize;
            elementItemSize = elementList.size();
            final StringBuilder builder = new StringBuilder();

            Object element;
            builder.append(" ROW(");
            for (int i = 0; i < elementItemSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                element = elementList.get(i);
                if (element == null) {
                    builder.append(_Constant.SPACE_NULL);
                } else {
                    if (!(element instanceof RowElement)) {
                        builder.append(_Constant.SPACE);
                    }
                    builder.append(element);
                }

            }//for
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//RowConstructor

    private static final class ImmutableRowConstructor extends RowConstructorExpression {

        private final int columnSize;

        private ImmutableRowConstructor(List<Object> elementList, int columnSize) {
            super(elementList);
            assert columnSize >= elementList.size();
            this.columnSize = columnSize;
        }

        @Override
        public int columnSize() {
            return this.columnSize;
        }


    }//ImmutableRowConstructor


}
