package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.SelectionSpec;
import io.army.criteria.TableField;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._Selection;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.meta.TypeMeta;
import io.army.util._ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BooleanSupplier;

abstract class DialectFunctionUtils extends FunctionUtils {

    DialectFunctionUtils() {
    }


    static Functions._TabularFunction compositeTabularFunc(String name, List<?> argList, List<? extends Selection> selectionList) {
        return new CompositeTabularFunction(name, argList, selectionList, null);
    }

    static Functions._TabularFunction compositeTabularFunc(String name, List<?> argList, List<? extends Selection> selectionList,
                                                           Map<String, Selection> selectionMap) {
        return new CompositeTabularFunction(name, argList, selectionList, selectionMap);
    }

    static Functions._TabularWithOrdinalityFunction oneArgTabularFunc(final String name, final Expression one,
                                                                      final List<Selection> funcFieldList) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgTabularFunction(name, one, funcFieldList);
    }

    static Functions._TabularWithOrdinalityFunction twoArgTabularFunc(final String name, final Expression one,
                                                                      final Expression two,
                                                                      final List<Selection> funcFieldList) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgTabularFunction(name, one, two, funcFieldList);
    }

    static Functions._ColumnWithOrdinalityFunction oneArgColumnFunction(final String name, final Expression one,
                                                                        String defaultSelectionAlias,
                                                                        final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgColumnFunction(name, defaultSelectionAlias, one, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction twoArgColumnFunction(
            final String name, final Expression one, final Expression two, final String defaultSelectionAlias,
            final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgColumnFunction(name, defaultSelectionAlias, one, two, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction threeArgColumnFunction(
            final String name, final Expression one, final Expression two, final Expression three,
            final String defaultSelectionAlias, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgColumnFunction(name, defaultSelectionAlias, one, two, three, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction fourArgColumnFunction(
            final String name, final Expression one, final Expression two, final Expression three,
            final Expression four, final String defaultSelectionAlias, final TypeMeta returnType) {

        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        return new FourArgColumnFunction(name, defaultSelectionAlias, one, two, three, four, returnType);
    }

    /**
     * <p>
     * This class don't support {@link Functions._WithOrdinalityClause}.
     * </p>
     */
    private static final class CompositeTabularFunction implements Functions._TabularFunction, _DerivedTable,
            _SelfDescribed {

        private final String name;

        private final List<?> argList;

        private final List<? extends Selection> selectionList;

        private Map<String, Selection> selectionMap;

        private CompositeTabularFunction(String name, List<?> argList, List<? extends Selection> selectionList,
                                         @Nullable Map<String, Selection> selectionMap) {
            assert selectionMap == null || selectionMap.size() == selectionList.size();
            this.name = name;
            this.argList = argList;
            this.selectionList = selectionList;
            this.selectionMap = selectionMap;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            FunctionUtils.appendComplexArg(this.argList, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public Selection refSelection(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = FunctionUtils.createSelectionMapFrom(null, this.selectionList);
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionList;
        }

    }//CompositeTabularFunction


    private static abstract class TabularSqlFunction implements _DerivedTable,
            Functions._TabularFunction, _SelfDescribed {

        static final String ORDINALITY = "ordinality";

        private static final String SPACE_WITH_ORDINALITY = " WITH ORDINALITY";

        final CriteriaContext outerContext;

        final String name;

        private TabularSqlFunction(String name) {
            this.name = name;
            this.outerContext = ContextStack.peek();
        }

        private TabularSqlFunction(ColumnFunction columnFunction) {
            this.outerContext = columnFunction.outerContext;
            this.name = columnFunction.name;
        }


        @Override
        public final void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            if (context.isLowerFunctionName()) {
                sqlBuilder.append(this.name.toLowerCase(Locale.ROOT));
            } else {
                sqlBuilder.append(this.name);
            }
            sqlBuilder.append(_Constant.LEFT_PAREN);

            appendArg(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            if (this.isWithOrdinality()) {
                sqlBuilder.append(SPACE_WITH_ORDINALITY);
            }

        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            argToString(builder);
            builder.append(_Constant.SPACE_RIGHT_PAREN);
            if (this.isWithOrdinality()) {
                builder.append(SPACE_WITH_ORDINALITY);
            }
            return builder.toString();
        }

        abstract void appendArg(_SqlContext context);

        abstract void argToString(StringBuilder builder);

        private boolean isWithOrdinality() {
            final boolean withOrdinality;
            if (this instanceof MultiFieldTabularFunction) {
                final Boolean v = ((MultiFieldTabularFunction) this).ordinality;
                withOrdinality = v != null && v;
            } else if (this instanceof ColumnTabularFunctionWrapper) {
                withOrdinality = ((ColumnTabularFunctionWrapper) this).withOrdinality;
            } else if (this instanceof ColumnFunction) {
                withOrdinality = false;
            } else {
                // no bug, never here
                throw new IllegalArgumentException();
            }
            return withOrdinality;
        }


    }//TabularSqlFunction

    private static final class ColumnTabularFunctionWrapper extends TabularSqlFunction {

        private final ColumnFunction columnFunction;

        private final List<Selection> funcFieldList;

        private final boolean withOrdinality;

        private Map<String, Selection> selectionMap;

        private ColumnTabularFunctionWrapper(ColumnFunction columnFunction, boolean withOrdinality) {
            super(columnFunction);
            this.columnFunction = columnFunction;
            final List<Selection> fieldList = _Collections.arrayList(2);
            fieldList.add(ArmySelections.forName(columnFunction.name.toLowerCase(Locale.ROOT), columnFunction.returnType));
            fieldList.add(ArmySelections.forName(ORDINALITY, LongType.INSTANCE));

            this.funcFieldList = _ArrayUtils.asUnmodifiableList(fieldList);
            this.withOrdinality = withOrdinality;
        }

        @Override
        void appendArg(_SqlContext context) {
            this.columnFunction.appendArg(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            this.columnFunction.argToString(builder);
        }

        @Override
        public Selection refSelection(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = createSelectionMapFrom(this.outerContext, this.funcFieldList);
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.funcFieldList;
        }


    }//ColumnTabularFunctionWrapper

    private static abstract class ColumnFunction extends TabularSqlFunction
            implements Functions._ColumnWithOrdinalityFunction, _Selection {

        private final TypeMeta returnType;

        private final String defaultSelectionAlias;

        private String userDefinedAlias;

        private TypeMeta userDefinedType;

        private Boolean ordinality;

        private ColumnFunction(String name, String defaultSelectionAlias, TypeMeta returnType) {
            super(name);
            this.defaultSelectionAlias = defaultSelectionAlias;
            this.returnType = returnType;
        }

        @Override
        public final Selection as(final @Nullable String selectionAlas) {
            if (this.ordinality != null || this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (selectionAlas == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_StringUtils.hasText(selectionAlas)) {
                throw ContextStack.clearStackAnd(_Exceptions::selectionAliasNoText);
            }
            this.userDefinedAlias = selectionAlas;
            return this;
        }

        @Override
        public final String alias() {
            String selectionName = this.userDefinedAlias;
            if (selectionName == null) {
                selectionName = this.defaultSelectionAlias;
                this.userDefinedAlias = selectionName;
            }
            return selectionName;
        }


        @Override
        public final void appendSelectItem(final _SqlContext context) {
            if (this.ordinality != null) {
                throw _Exceptions.castCriteriaApi();
            }
            this.appendSql(context);

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_AS_SPACE);

            context.parser().identifier(this.alias(), sqlBuilder);

        }

        @Override
        public final Functions._TabularFunction withOrdinality() {
            if (this.ordinality != null || this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            this.ordinality = Boolean.TRUE;
            return new ColumnTabularFunctionWrapper(this, true);
        }

        @Override
        public final Functions._TabularFunction ifWithOrdinality(BooleanSupplier predicate) {
            if (this.ordinality != null || this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final boolean withOrdinality;
            withOrdinality = predicate.getAsBoolean();
            this.ordinality = withOrdinality;
            return new ColumnTabularFunctionWrapper(this, withOrdinality);
        }

        @Override
        public final Selection refSelection(final @Nullable String name) {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            return name.equals(this.alias()) ? this : null;
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return Collections.singletonList(this);
        }

        @Override
        public final TypeMeta typeMeta() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            TypeMeta type = this.userDefinedType;
            if (type == null) {
                type = this.returnType;
                this.userDefinedType = type;
            }
            return type;
        }

        @Override
        public final SelectionSpec mapTo(final @Nullable TypeMeta mapType) {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (this.userDefinedType != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (mapType == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.userDefinedType = mapType;
            return this;
        }

        @Override
        public final TableField tableField() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            // always null
            return null;
        }

        @Override
        public final Expression underlyingExp() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            // always null
            return null;
        }


    }//ScalarTabularFunction

    static final class OneArgColumnFunction extends ColumnFunction {

        private final ArmyExpression one;

        /**
         * @see #oneArgColumnFunction(String, Expression, String, TypeMeta)
         */
        private OneArgColumnFunction(String name, String defaultSelectionAlias, Expression one, TypeMeta returnType) {
            super(name, defaultSelectionAlias, returnType);
            this.one = (ArmyExpression) one;
        }


        @Override
        void appendArg(final _SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgScalarTabularFunction

    static final class TwoArgColumnFunction extends ColumnFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private TwoArgColumnFunction(String name, String defaultSelectionAlias, Expression one, Expression two,
                                     TypeMeta returnType) {
            super(name, defaultSelectionAlias, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
        }


        @Override
        void appendArg(final _SqlContext context) {
            this.one.appendSql(context);
            context.sqlBuilder().append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgScalarTabularFunction

    static final class ThreeArgColumnFunction extends ColumnFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private ThreeArgColumnFunction(String name, String defaultSelectionAlias, Expression one, Expression two,
                                       Expression three, TypeMeta returnType) {
            super(name, defaultSelectionAlias, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
        }


        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three);
        }


    }//ThreeArgScalarTabularFunction

    static final class FourArgColumnFunction extends ColumnFunction {

        private final ArmyExpression one;

        private final ArmyExpression two;

        private final ArmyExpression three;

        private final ArmyExpression four;

        /**
         * @see #fourArgColumnFunction(String, Expression, Expression, Expression, Expression, String, TypeMeta)
         */
        private FourArgColumnFunction(String name, String defaultSelectionAlias, Expression one, Expression two,
                                      Expression three, Expression four, TypeMeta returnType) {
            super(name, defaultSelectionAlias, returnType);
            this.one = (ArmyExpression) one;
            this.two = (ArmyExpression) two;
            this.three = (ArmyExpression) three;
            this.four = (ArmyExpression) four;
        }


        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();

            this.one.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.two.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.three.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_COMMA);

            this.four.appendSql(context);

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.three)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.four);
        }


    }//FourArgScalarTabularFunction

    private static abstract class MultiFieldTabularFunction extends TabularSqlFunction
            implements Functions._TabularWithOrdinalityFunction {


        private final List<Selection> funcFieldList;

        private List<Selection> actualFuncFieldList;

        private Map<String, Selection> fieldMap;

        private Boolean ordinality;


        private MultiFieldTabularFunction(String name, List<Selection> funcFieldList) {
            super(name);
            assert funcFieldList.size() > 0;
            this.funcFieldList = Collections.unmodifiableList(funcFieldList);
            this.actualFuncFieldList = this.funcFieldList;
        }

        @Override
        public final Selection refSelection(final @Nullable String name) {
            if (this.ordinality == null) {
                this.ordinality = Boolean.FALSE;
            }
            if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            Map<String, Selection> fieldMap = this.fieldMap;
            if (fieldMap == null) {
                fieldMap = createSelectionMapFrom(this.outerContext, this.refAllSelection());// must invoke this.refAllSelection()
                this.fieldMap = fieldMap;
            }
            return fieldMap.get(name);
        }

        @Override
        public final List<? extends Selection> refAllSelection() {

            final Boolean ordinality = this.ordinality;
            if (ordinality == null) {
                this.ordinality = Boolean.FALSE;
            }

            final List<Selection> actualFieldList = this.actualFuncFieldList;
            if (actualFieldList == this.funcFieldList) {
                assert ordinality == null || !ordinality;
            } else {
                final int actualFieldsSize;
                actualFieldsSize = actualFieldList.size();

                assert ordinality != null && ordinality;
                assert actualFieldsSize - this.funcFieldList.size() == 1;
                assert actualFieldList.get(actualFieldsSize - 1).alias().equals(ORDINALITY);
            }
            return actualFieldList;
        }

        @Override
        public final Functions._TabularFunction withOrdinality() {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final List<Selection> list = new ArrayList<>(this.funcFieldList.size() + 1);
            list.addAll(this.funcFieldList);
            list.add(ArmySelections.forName(ORDINALITY, LongType.INSTANCE));
            this.actualFuncFieldList = Collections.unmodifiableList(list);

            this.ordinality = Boolean.TRUE;
            return this;
        }

        @Override
        public final Functions._TabularFunction ifWithOrdinality(final BooleanSupplier predicate) {
            if (this.ordinality != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (predicate.getAsBoolean()) {
                this.withOrdinality();
            } else {
                this.ordinality = Boolean.FALSE;
            }
            return this;
        }


    }//MultiFieldTabularFunction

    private static final class OneArgTabularFunction extends MultiFieldTabularFunction {

        private final FunctionArg one;

        /**
         * @see DialectFunctionUtils#oneArgTabularFunc(String, Expression, List)
         */
        private OneArgTabularFunction(String name, Expression one, List<Selection> funcFieldList) {
            super(name, funcFieldList);
            this.one = (FunctionArg) one;
        }


        @Override
        void appendArg(_SqlContext context) {
            this.one.appendSql(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one);
        }


    }//OneArgTabularFunction

    private static final class TwoArgTabularFunction extends MultiFieldTabularFunction {

        private final FunctionArg one;

        private final FunctionArg two;

        /**
         * @see DialectFunctionUtils#twoArgTabularFunc(String, Expression, Expression, List)
         */
        private TwoArgTabularFunction(String name, Expression one, Expression two, List<Selection> funcFieldList) {
            super(name, funcFieldList);
            this.one = (FunctionArg) one;
            this.two = (FunctionArg) two;
        }


        @Override
        void appendArg(_SqlContext context) {
            this.one.appendSql(context);
            context.sqlBuilder().append(_Constant.SPACE_COMMA);
            this.two.appendSql(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            builder.append(this.one)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.two);
        }


    }//TwoArgTabularFunction


}
