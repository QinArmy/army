package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._FunctionField;
import io.army.criteria.impl.inner._SelectionGroup;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect.DialectParser;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.NoCastTextType;
import io.army.mapping.optional.CompositeTypeField;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.SqlType;
import io.army.util._ArrayUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BooleanSupplier;

abstract class DialectFunctionUtils extends FunctionUtils {

    DialectFunctionUtils() {
    }


    static SimpleExpression jsonTableRowFunc(String name, _SelectionGroup._TableFieldGroup group,
                                             MappingType returnType) {
        return new JsonObjectTableRowFunc(name, group, returnType);
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
                                                                        final @Nullable String fieldName,
                                                                        final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgColumnFunction(name, fieldName, one, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction twoArgColumnFunction(
            final String name, final Expression one, final Expression two, final @Nullable String fieldName,
            final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        }
        return new TwoArgColumnFunction(name, fieldName, one, two, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction threeArgColumnFunction(
            final String name, final Expression one, final Expression two, final Expression three,
            final @Nullable String fieldName, final TypeMeta returnType) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        }
        return new ThreeArgColumnFunction(name, fieldName, one, two, three, returnType);
    }

    static Functions._ColumnWithOrdinalityFunction fourArgColumnFunction(
            final String name, final Expression one, final Expression two, final Expression three,
            final Expression four, final @Nullable String fieldName, final TypeMeta returnType) {

        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        } else if (!(two instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, two);
        } else if (!(three instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, three);
        } else if (!(four instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, four);
        }
        return new FourArgColumnFunction(name, fieldName, one, two, three, four, returnType);
    }

    static List<Selection> compositeFieldList(final String name, final Expression compositeExp) {
        final MappingType type;
        type = compositeExp.typeMeta().mappingType();
        if (!(type instanceof MappingType.SqlCompositeType)) {
            throw CriteriaUtils.notCompositeType(name, compositeExp);
        }
        final List<CompositeTypeField> fieldList;
        fieldList = ((MappingType.SqlCompositeType) type).fieldList();
        final int fieldSize;
        if ((fieldSize = fieldList.size()) == 0) {
            String m = String.format("%s's fieldMap() return empty.", type);
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        final List<Selection> selectionList;
        selectionList = _Collections.arrayList(fieldSize);
        for (CompositeTypeField field : fieldList) {
            selectionList.add(ArmySelections.forName(field.name, field.type));
        }
        return selectionList;
    }

    /**
     * <p>
     * For {@link UndoneFunction}
     * </p>
     */
    static _FunctionField funcField(final @Nullable String name, final @Nullable MappingType type,
                                    final Class<? extends SqlType> sqlTypeClass) {
        if (name == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (type == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("function field name must have text.");
        }
        return new FunctionField(name, type, sqlTypeClass);
    }

    static UndoneFunction oneArgUndoneFunc(final String name, final Expression one) {
        if (!(one instanceof FunctionArg.SingleFunctionArg)) {
            throw CriteriaUtils.funcArgError(name, one);
        }
        return new OneArgUndoneFunction(name, one);
    }


    /*-------------------below inner class -------------------*/

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
        public String name() {
            return this.name;
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
        public final String name() {
            return this.name;
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
                withOrdinality = true;
            } else if (this instanceof ColumnFunction) {
                withOrdinality = false;
            } else {
                // no bug, never here
                throw new IllegalArgumentException();
            }
            return withOrdinality;
        }


    }//TabularSqlFunction

    private static final class ColumnTabularFunctionWrapper extends TabularSqlFunction
            implements UndoneColumnFunc {

        private final ColumnFunction columnFunction;

        private final List<Selection> funcFieldList;

        private Map<String, Selection> selectionMap;

        private ColumnTabularFunctionWrapper(ColumnFunction columnFunction) {
            super(columnFunction);
            this.columnFunction = columnFunction;
            this.funcFieldList = _ArrayUtils.asUnmodifiableList(columnFunction.field,
                    ArmySelections.forName(ORDINALITY, LongType.INSTANCE)
            );
        }

        @Override
        public boolean isNoNameField() {
            return this.columnFunction.isNoNameField();
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

        @Override
        void appendArg(_SqlContext context) {
            this.columnFunction.appendArg(context);
        }

        @Override
        void argToString(StringBuilder builder) {
            this.columnFunction.argToString(builder);
        }


    }//ColumnTabularFunctionWrapper

    private static abstract class ColumnFunction extends TabularSqlFunction
            implements UndoneColumnFunc, Functions._ColumnWithOrdinalityFunction,
            Selection {

        private final TypeMeta returnType;

        private final Selection field;

        private String userDefinedAlias;

        private TypeMeta userDefinedType;

        private ColumnFunction(String name, @Nullable String fieldName, TypeMeta returnType) {
            super(name);
            if (fieldName == null) {
                this.field = this;
            } else {
                this.field = ArmySelections.forName(fieldName, returnType);
            }
            this.returnType = returnType;
        }


        @Override
        public final boolean isNoNameField() {
            return this.field == this;
        }

        @Override
        public final Selection as(final @Nullable String selectionAlas) {
            if (this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (selectionAlas == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (!_StringUtils.hasText(selectionAlas)) {
                throw ContextStack.criteriaError(this.outerContext, _Exceptions::selectionAliasNoText);
            }
            this.userDefinedAlias = selectionAlas;
            return ArmySelections.forColumnFunc(this, selectionAlas);
        }

        @Override
        public final String alias() {
            //no bug,never here
            throw new UnsupportedOperationException();
        }

        @Override
        public final Functions._TabularFunction withOrdinality() {
            if (this.userDefinedType != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            this.userDefinedType = this.returnType;
            return new ColumnTabularFunctionWrapper(this);
        }

        @Override
        public final Functions._TabularFunction ifWithOrdinality(final BooleanSupplier predicate) {
            if (this.userDefinedType != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            final Functions._TabularFunction function;
            if (predicate.getAsBoolean()) {
                function = new ColumnTabularFunctionWrapper(this);
            } else {
                function = this;
            }
            this.userDefinedType = this.returnType;
            return function;
        }

        @Override
        public final Selection refSelection(final @Nullable String name) {
            final Selection field = this.field, selection;
            if (field == this) {
                //no bug,never here
                throw new UnsupportedOperationException();
            } else if (this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (name == null) {
                throw ContextStack.nullPointer(this.outerContext);
            } else if (name.equals(field.alias())) {
                selection = this;
            } else {
                selection = null;
            }
            return selection;
        }

        @Override
        public final List<? extends Selection> refAllSelection() {
            if (this.userDefinedAlias != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            }
            return _Collections.singletonList(this.field);
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.returnType;
        }

        @Override
        public final SelectionSpec mapTo(final @Nullable TypeMeta typeMeta) {
            if (this.userDefinedType != null) {
                throw ContextStack.castCriteriaApi(this.outerContext);
            } else if (typeMeta == null) {
                throw ContextStack.nullPointer(this.outerContext);
            }
            this.userDefinedType = typeMeta;
            return this;
        }


    }//ScalarTabularFunction

    static final class OneArgColumnFunction extends ColumnFunction {

        private final ArmyExpression one;

        /**
         * @see #oneArgColumnFunction(String, Expression, String, TypeMeta)
         */
        private OneArgColumnFunction(String name, @Nullable String fieldName, Expression one, TypeMeta returnType) {
            super(name, fieldName, returnType);
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

        private TwoArgColumnFunction(String name, @Nullable String fieldName, Expression one, Expression two,
                                     TypeMeta returnType) {
            super(name, fieldName, returnType);
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

        private ThreeArgColumnFunction(String name, @Nullable String fieldName, Expression one, Expression two,
                                       Expression three, TypeMeta returnType) {
            super(name, fieldName, returnType);
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
        private FourArgColumnFunction(String name, @Nullable String fieldName, Expression one, Expression two,
                                      Expression three, Expression four, TypeMeta returnType) {
            super(name, fieldName, returnType);
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

    private static abstract class TabularUndoneFunction implements UndoneFunction, _SelfDescribed {

        private final String name;

        private TabularUndoneFunction(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            if (context.isLowerFunctionName()) {
                sqlBuilder.append(this.name.toLowerCase(Locale.ROOT));
            } else {
                sqlBuilder.append(this.name);
            }

            sqlBuilder.append(_Constant.LEFT_PAREN);

            this.appendArg(context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        abstract void appendArg(_SqlContext context);

        abstract void argToString(StringBuilder builder);


        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            this.argToString(builder);
            builder.append(_Constant.SPACE_RIGHT_PAREN);
            return builder.toString();
        }


    }//TabularUndoneFunction


    private static final class OneArgUndoneFunction extends TabularUndoneFunction {

        private final ArmyExpression one;

        /**
         * @see #oneArgUndoneFunc(String, Expression)
         */
        private OneArgUndoneFunction(String name, Expression one) {
            super(name);
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


    }//OneArgUndoneFunction


    /**
     * <p>
     * For {@link UndoneFunction}
     * </p>
     */
    private static final class FunctionField extends OperationDataField implements _FunctionField {

        private final String name;

        private final MappingType type;

        private final Class<? extends SqlType> sqlTypeClass;

        /**
         * @see #funcField(String, MappingType, Class)
         */
        private FunctionField(String name, MappingType type, Class<? extends SqlType> sqlTypeClass) {
            this.name = name;
            this.type = type;
            this.sqlTypeClass = sqlTypeClass;
        }

        @Override
        public void appendSelectItem(_SqlContext context) {
            // no bug,never here
            throw new UnsupportedOperationException("invoking error");
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);

            final DialectParser parser;
            parser = context.parser();

            parser.identifier(this.name, sqlBuilder)
                    .append(_Constant.SPACE);

            parser.typeName(this.type, sqlBuilder);

        }

        @Override
        public String fieldName() {
            return this.name;
        }

        @Override
        public String alias() {
            return this.name;
        }

        /**
         * @return function field must return {@link MappingType}, not {@link TableField}
         */
        @Override
        public MappingType typeMeta() {
            return this.type;
        }

        @Override
        public TableField tableField() {
            // always null
            return null;
        }

        @Override
        public Expression underlyingExp() {
            // always null
            return null;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.SPACE)
                    .append(this.type)
                    .toString();
        }


    }//FunctionField


    private static final class JsonObjectTableRowFunc extends FunctionUtils.FunctionExpression {

        private final _SelectionGroup._TableFieldGroup group;

        private JsonObjectTableRowFunc(String name, _SelectionGroup._TableFieldGroup group,
                                       MappingType returnType) {
            super(name, returnType);
            this.group = group;
        }

        @Override
        void appendArg(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final String tableAlias;
            tableAlias = this.group.tableAlias();

            final List<? extends Selection> selectionList;
            selectionList = this.group.selectionList();
            final int selectionSize;
            selectionSize = selectionList.size();
            FieldMeta<?> field;
            for (int i = 0; i < selectionSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    sqlBuilder.append(_Constant.SPACE);
                }
                field = (FieldMeta<?>) selectionList.get(i);

                context.appendLiteral(NoCastTextType.INSTANCE, field.columnName()); // here use column name not field name
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
                context.appendField(tableAlias, field);
            }
        }

        @Override
        void argToString(final StringBuilder builder) {
            final String tableAlias;
            tableAlias = this.group.tableAlias();

            final List<? extends Selection> selectionList;
            selectionList = this.group.selectionList();

            final int selectionSize;
            selectionSize = selectionList.size();

            FieldMeta<?> field;
            for (int i = 0; i < selectionSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    builder.append(_Constant.SPACE);
                }
                field = (FieldMeta<?>) selectionList.get(i);

                builder.append(_Constant.QUOTE)
                        .append(field.columnName()) // here use column name not field name
                        .append(_Constant.QUOTE)
                        .append(_Constant.SPACE_COMMA_SPACE)
                        .append(tableAlias)
                        .append(_Constant.POINT)
                        .append(field.columnName());
            }

        }

    }//JsonObjectTableFunc


}
