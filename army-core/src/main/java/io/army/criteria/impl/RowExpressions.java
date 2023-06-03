package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect.Database;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.NoCastIntegerType;
import io.army.mapping.NoCastTextType;
import io.army.mapping._MappingFactory;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class hold the methods that create {@link io.army.criteria.RowExpression}.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
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
        final RowExpression row;
        if (element instanceof RowElement.DelayElement) {
            row = new DelayRowConstructor(_Collections.singletonList(element));
        } else {
            final Object actualElement;
            if (element instanceof Supplier) {
                actualElement = ((Supplier<?>) element).get();
            } else {
                actualElement = element;
            }
            row = new ImmutableRowConstructor(_Collections.singletonList(actualElement),
                    rowElementColumnSize(actualElement)
            );
        }
        return row;
    }

    static RowExpression row(final Object element1, final Object element2) {
        final RowExpression row;
        if ((element1 instanceof RowElement.DelayElement && ((RowElement.DelayElement) element1).isDelay())
                || (element2 instanceof RowElement.DelayElement && ((RowElement.DelayElement) element2).isDelay())) {
            row = new DelayRowConstructor(ArrayUtils.asUnmodifiableList(element1, element2));
        } else {
            final int columnSize;
            columnSize = rowElementColumnSize(element1) + rowElementColumnSize(element2);
            row = new ImmutableRowConstructor(ArrayUtils.asUnmodifiableList(element1, element2), columnSize);
        }
        return row;
    }

    static RowExpression row(final Object element1, final Object element2, final Object element3, final Object... rest) {
        boolean delay;
        delay = element1 instanceof RowElement.DelayElement && ((RowElement.DelayElement) element1).isDelay();
        if (!delay) {
            delay = element2 instanceof RowElement.DelayElement && ((RowElement.DelayElement) element2).isDelay();
        }
        if (!delay) {
            delay = element3 instanceof RowElement.DelayElement && ((RowElement.DelayElement) element3).isDelay();
        }

        int columnSize = 0;

        if (!delay) {
            columnSize += rowElementColumnSize(element1);
            columnSize += rowElementColumnSize(element2);
            columnSize += rowElementColumnSize(element3);
        }

        final List<Object> elementList = _Collections.arrayList(3 + rest.length);
        elementList.add(element1);
        elementList.add(element2);
        elementList.add(element3);

        for (Object e : rest) {
            elementList.add(e);
            if (!delay) {
                delay = e instanceof RowElement.DelayElement && ((RowElement.DelayElement) e).isDelay();
            }
            if (!delay) {
                columnSize += rowElementColumnSize(e);
            }
        }

        final RowExpression row;
        if (delay) {
            row = new DelayRowConstructor(elementList);
        } else {
            row = new ImmutableRowConstructor(elementList, columnSize);
        }
        return row;
    }

    static RowExpression row(Consumer<Consumer<Object>> consumer) {
        final List<Object> elementList = _Collections.arrayList();
        final int[] columnSizeHolder = new int[]{0};
        consumer.accept(e -> {
            if (columnSizeHolder[0] > -1) {
                if (e instanceof RowElement.DelayElement && ((RowElement.DelayElement) e).isDelay()) {
                    columnSizeHolder[0] = -1;
                } else {
                    columnSizeHolder[0] += rowElementColumnSize(e);
                }
            }
            elementList.add(e);
        });

        final int columnSize = columnSizeHolder[0];
        final RowExpression row;
        if (columnSize < 0) {
            row = new DelayRowConstructor(elementList);
        } else {
            row = new ImmutableRowConstructor(elementList, columnSize);
        }
        return row;
    }


    static void validateColumnSize(final SQLColumnSet left, final SQLColumnSet right) {
        if (right instanceof SubQuery) {
            Expressions.validateSubQueryContext((SubQuery) right);
        }
        if (isDelayColumnSet(left, right)) {
            final ColumnSetValidator validator = new ColumnSetValidator(left, right);
            ContextStack.addEndLise(validator::onContextEnd, validator::columnSetIsUnknown);
        } else {
            doValidateColumnSize(left, right);
        }
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


    private static boolean isDelayColumnSet(final SQLColumnSet left, final SQLColumnSet right) {
        final boolean leftDelay, rightDelay;
        leftDelay = left instanceof RowElement.DelayElement && ((RowElement.DelayElement) left).isDelay();
        rightDelay = right instanceof RowElement.DelayElement && ((RowElement.DelayElement) right).isDelay();
        return leftDelay || rightDelay;
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


    private static final class ColumnSetValidator {

        private final SQLColumnSet left;

        private final SQLColumnSet right;


        /**
         * @see #validateColumnSize(SQLColumnSet, SQLColumnSet)
         */
        private ColumnSetValidator(SQLColumnSet left, SQLColumnSet right) {
            this.left = left;
            this.right = right;
        }

        void onContextEnd() {
            final SQLColumnSet left = this.left, right = this.right;
            if (isDelayColumnSet(left, right)) {
                ContextStack.addEndLise(this::onContextEnd, this::columnSetIsUnknown);
            } else {
                doValidateColumnSize(left, right);
            }
        }


        private CriteriaException columnSetIsUnknown() {
            final SQLColumnSet left = this.left, right = this.right;
            final boolean leftDelay, rightDelay;
            leftDelay = left instanceof RowElement.DelayElement && ((RowElement.DelayElement) left).isDelay();
            rightDelay = right instanceof RowElement.DelayElement && ((RowElement.DelayElement) right).isDelay();
            final String m;
            if (leftDelay) {
                m = String.format("left operand %s is unknown.", left);
            } else if (rightDelay) {
                m = String.format("right operand %s is unknown.", right);
            } else {
                m = String.format("unknown operand %s and %s", left, right);
            }
            return new CriteriaException(m);

        }


    }//ColumnSetValidator

    private static abstract class RowConstructor extends OperationRowExpression {

        final List<Object> elementList;


        private RowConstructor(final List<Object> elementList) {
            this.elementList = elementList;
        }


        @Override
        public final void appendSql(final _SqlContext context) {

            final List<Object> elementList = this.elementList;
            final int elementItemSize;
            elementItemSize = elementList.size();
            final Database database;
            database = context.database();
            if (elementItemSize == 0 && database == Database.MySQL) {
                String m = String.format("error ,%s don't support empty row", database);
                throw new CriteriaException(m);
            }

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final DialectParser parser;
            parser = context.parser();

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
                    parser.subQuery((SubQuery) element, context);
                    outputColumnCount += ((ArmySubQuery) element).refAllSelection().size();
                } else if (element instanceof ArmySQLExpression) {
                    ((ArmySQLExpression) element).appendSql(context);
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

    private static final class ImmutableRowConstructor extends RowConstructor {

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

    private static final class DelayRowConstructor extends RowConstructor
            implements RowElement.DelayElement {

        private int columnSize = -1;

        private DelayRowConstructor(List<Object> elementList) {
            super(elementList);
        }

        @Override
        public int columnSize() {
            int columnSize = this.columnSize;
            if (columnSize > 0) {
                return columnSize;
            }

            int count = 0;
            for (Object element : this.elementList) {
                if (element instanceof RowElement.DelayElement && ((RowElement.DelayElement) element).isDelay()) {
                    // no bug,never here
                    String m = String.format("error,%s is delay row expression.", element);
                    throw ContextStack.clearStackAndCriteriaError(m);
                } else if (!(element instanceof RowElement)) {
                    count++;
                } else if (element instanceof SubQuery) {
                    count += ((ArmySubQuery) element).refAllSelection().size();
                } else if (element instanceof RowExpression) {
                    count += ((ArmyRowExpression) element).columnSize();
                } else {
                    // no bug,never here
                    throw _Exceptions.unknownRowElement(element);
                }
            }
            this.columnSize = count;
            return count;
        }

        @Override
        public boolean isDelay() {
            boolean match = false;
            for (Object element : this.elementList) {
                if (element instanceof RowElement.DelayElement && ((RowElement.DelayElement) element).isDelay()) {
                    match = true;
                    break;
                }
            }
            return match;
        }


    }//DelayRowConstructor


}
