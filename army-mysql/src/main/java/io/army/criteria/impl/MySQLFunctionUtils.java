package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLCharset;
import io.army.criteria.mysql.MySQLFunction;
import io.army.criteria.mysql.MySQLWindow;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.mysql.MySQLDialect;
import io.army.mapping.*;
import io.army.mapping.mysql.MySqlBitType;
import io.army.mapping.spatial.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.TypeMeta;
import io.army.sqltype.MySQLType;
import io.army.stmt.SimpleStmt;
import io.army.stmt.SingleParam;
import io.army.stmt.Stmt;
import io.army.util.ClassUtils;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MySQLFunctionUtils extends DialectFunctionUtils {

    private MySQLFunctionUtils() {
    }


    static MySQLWindowFunctions._OverSpec noArgWindowFunc(String name, TypeMeta returnType) {
        return new NoArgWindowFunction(name, returnType);
    }

    static MySQLWindowFunctions._OverSpec oneArgWindowFunc(
            String name, Expression arg, TypeMeta returnType) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgWindowFunction(name, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._OverSpec twoArgWindowFunc(
            String name, Expression one, Expression two, TypeMeta returnType) {
        return new MultiArgWindowFunction(name, null, twoExpList(name, one, two), returnType);
    }

    static MySQLWindowFunctions._OverSpec threeArgWindow(String name, Expression one, Expression two, Expression three,
                                                         TypeMeta returnType) {
        return new MultiArgWindowFunction(name, null, threeExpList(name, one, two, three), returnType);
    }


    static MySQLWindowFunctions._FromFirstLastOverSpec twoArgFromFirstWindowFunc(String name, Expression one,
                                                                                 Expression two, TypeMeta returnType) {
        return new FromFirstLastMultiArgWindowFunc(name, twoExpList(name, one, two), returnType);
    }


    static MySQLWindowFunctions._ItemAggregateWindowFunc oneArgAggregate(String name, Expression arg,
                                                                         TypeMeta returnType) {
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgAggregateWindowFunc(name, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._ItemAggregateWindowFunc oneArgAggregate(String name, @Nullable SQLWords option
            , Expression arg, TypeMeta returnType) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        if (arg instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, arg);
        }
        return new OneArgOptionAggregateWindowFunc(name, option, (ArmyExpression) arg, returnType);
    }

    static MySQLWindowFunctions._ItemAggregateWindowFunc multiArgAggregateWindowFunc(
            String name, @Nullable SQLWords option, List<Expression> argList, TypeMeta returnType) {
        assert option == null || option == SQLs.DISTINCT || option == MySQLs.DISTINCT;
        final List<ArmyExpression> expList = new ArrayList<>(argList.size());
        for (Expression arg : argList) {
            expList.add((ArmyExpression) arg);
        }
        return new MultiArgAggregateWindowFunc(name, option, expList, returnType);
    }


    static JsonValueClause jsonValueInnerClause() {
        return new JsonValueClause();
    }

    static SimpleExpression jsonValueFunc(Expression jsonDoc, Expression path, JsonValueClause clause) {
        final String name = "JSON_VALUE";
        if (jsonDoc instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, jsonDoc);
        } else if (path instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, path);
        }
        return new JsonValueFunction((ArmyExpression) jsonDoc, (ArmyExpression) path, clause);
    }

    static MySQLJsonTableColumns jsonTableColumns() {
        return new MySQLJsonTableColumns();
    }

    /**
     * <p>Create jsonTable function.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html#function_json-table">JSON_TABLE(expr, path COLUMNS (column_list) [AS] alias)</a>
     */
    static Functions._TabularFunction jsonTable(final Object json, final Object path, final MySQLJsonTableColumns columns) {
        final Expression jsonExp, pathExp;
        if (json instanceof Expression) {
            jsonExp = (Expression) json;
        } else {
            jsonExp = SQLs.literal(JsonType.TEXT, json);
        }

        if (path instanceof String) {
            pathExp = SQLs.literal(StringType.INSTANCE, jsonExp);
        } else if (path instanceof Expression) {
            pathExp = (Expression) path;
        } else {
            String m = String.format("path must be %s or %s", String.class.getName(), Expression.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return new JsonTableFunc(jsonExp, pathExp, columns.endClause());
    }


    static GroupConcatInnerClause groupConcatClause() {
        return new GroupConcatInnerClause();
    }

    static Expression groupConcatFunc(final @Nullable SQLs.ArgDistinct distinct, final Expression exp
            , @Nullable GroupConcatInnerClause clause) {
        return new GroupConcatFunction(distinct, Collections.singletonList((ArmyExpression) exp), clause);
    }

    static Expression groupConcatFunc(final @Nullable SQLs.ArgDistinct distinct, final List<Expression> expList
            , @Nullable GroupConcatInnerClause clause) {
        final int expSize = expList.size();
        if (expSize == 0) {
            throw CriteriaUtils.funcArgError("GROUP_CONCAT", expList);
        }
        final List<ArmyExpression> argList = new ArrayList<>(expSize);
        appendExpList(argList, expList);
        return new GroupConcatFunction(distinct, argList, clause);
    }


    static SimpleExpression statementDigest(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        final String name = "STATEMENT_DIGEST";
        assertPrimaryStatement(statement, name);
        return new StatementDigestFunc(name, statement, visible, literal, StringType.INSTANCE);
    }

    static SimpleExpression statementDigestText(final PrimaryStatement statement, final Visible visible, final boolean literal) {
        final String name = "STATEMENT_DIGEST_TEXT";
        assertPrimaryStatement(statement, name);
        return new StatementDigestFunc(name, statement, visible, literal, StringType.INSTANCE);
    }


    /**
     * @see #statementDigest(PrimaryStatement, Visible, boolean)
     * @see #statementDigestText(PrimaryStatement, Visible, boolean)
     */
    private static void assertPrimaryStatement(final PrimaryStatement statement, final String funcName) {
        if (statement instanceof _BatchStatement
                || statement instanceof _Statement._ChildStatement
                || (statement instanceof _DomainUpdate
                && ((_DomainUpdate) statement).table() instanceof ChildTableMeta)
                || (statement instanceof _DomainDelete
                && ((_DomainDelete) statement).table() instanceof ChildTableMeta)) {
            String m = String.format("%s support only simple statement", funcName);
            throw ContextStack.criteriaError(ContextStack.peek(), m);
        }
    }

    private static MappingType mapType(final TypeDef typeDef) {
        final MySQLType dataType;
        if (typeDef instanceof MySQLType) {
            dataType = (MySQLType) typeDef;
        } else if (!(typeDef instanceof TypeDefs)) {
            throw ContextStack.clearStackAndCriteriaError("unknown TypeDef");
        } else if (((TypeDefs) typeDef).dataType instanceof MySQLType) {
            dataType = (MySQLType) ((TypeDefs) typeDef).dataType;
        } else {
            throw ContextStack.clearStackAndCriteriaError("unknown TypeDef");
        }

        final MappingType type;
        switch (dataType) {
            case BOOLEAN:
                type = BooleanType.INSTANCE;
                break;
            case TINYINT:
                type = ByteType.INSTANCE;
                break;
            case SMALLINT:
                type = ShortType.INSTANCE;
                break;
            case MEDIUMINT:
                type = MediumIntType.INSTANCE;
                break;
            case INT:
                type = IntegerType.INSTANCE;
                break;
            case BIGINT:
                type = LongType.INSTANCE;
                break;
            case DECIMAL:
                type = BigDecimalType.INSTANCE;
                break;
            case DOUBLE:
                type = DoubleType.INSTANCE;
                break;
            case FLOAT:
                type = FloatType.INSTANCE;
                break;

            case TINYINT_UNSIGNED:
                type = UnsignedByteType.INSTANCE;
                break;
            case SMALLINT_UNSIGNED:
                type = UnsignedShortType.INSTANCE;
                break;
            case MEDIUMINT_UNSIGNED:
                type = UnsignedMediumIntType.INSTANCE;
                break;
            case INT_UNSIGNED:
                type = UnsignedIntegerType.INSTANCE;
                break;
            case BIGINT_UNSIGNED:
                type = UnsignedLongType.INSTANCE;
                break;
            case DECIMAL_UNSIGNED:
                type = UnsignedBigDecimalType.INSTANCE;
                break;

            case TIME:
                type = LocalTimeType.INSTANCE;
                break;
            case DATE:
                type = LocalDateType.INSTANCE;
                break;
            case DATETIME:
                type = LocalDateTimeType.INSTANCE;
                break;
            case YEAR:
                type = YearType.INSTANCE;
                break;

            case CHAR:
                type = SqlCharType.INSTANCE;
                break;
            case VARCHAR:
            case SET:
            case ENUM:
                type = StringType.INSTANCE;
                break;
            case TINYTEXT:
                type = TinyTextType.INSTANCE;
                break;
            case TEXT:
                type = TextType.INSTANCE;
                break;
            case MEDIUMTEXT:
                type = MediumTextType.INSTANCE;
                break;
            case LONGTEXT:
                type = LongText.STRING;
                break;

            case JSON:
                type = JsonType.TEXT;
                break;
            case BIT:
                type = MySqlBitType.INSTANCE;
                break;

            case BINARY:
                type = BinaryType.INSTANCE;
                break;
            case VARBINARY:
                type = VarBinaryType.INSTANCE;
                break;
            case TINYBLOB:
                type = TinyBlobType.INSTANCE;
                break;
            case BLOB:
                type = BlobType.INSTANCE;
                break;
            case MEDIUMBLOB:
                type = MediumBlobType.INSTANCE;
                break;
            case LONGBLOB:
                type = LongBlobType.BYTE_ARRAY;
                break;

            case GEOMETRY:
                type = GeometryType.BINARY;
                break;
            case POINT:
                type = PointType.BINARY;
                break;
            case LINESTRING:
                type = LineStringType.BINARY;
                break;
            case POLYGON:
                type = PolygonType.BINARY;
                break;
            case MULTIPOINT:
                type = MultiPointType.BINARY;
                break;
            case MULTIPOLYGON:
                type = MultiPolygonType.BINARY;
                break;
            case MULTILINESTRING:
                type = MultiLineStringType.BINARY;
                break;
            case GEOMETRYCOLLECTION:
                type = GeometryCollectionType.BINARY;
                break;
            case NULL:
            case UNKNOWN:
            default:
                throw ContextStack.clearStackAndCriteriaError(String.format("error type %s", dataType));
        }
        return type;
    }


    private static abstract class MySQLWindowFunction extends WindowFunctionUtils.WindowFunction<MySQLWindow._PartitionBySpec>
            implements MySQLWindowFunctions._OverSpec, MySQLFunction {


        private MySQLWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }


        @Override
        final boolean isDontSupportWindow(final Dialect dialect) {
            if (!(dialect instanceof MySQLDialect)) {
                throw dialectError(dialect);
            }
            return MySQLDialect.MySQL80.compareWith((MySQLDialect) dialect) < 0;
        }

        @Override
        final MySQLWindow._PartitionBySpec createAnonymousWindow(@Nullable String existingWindowName) {
            return MySQLSupports.anonymousWindow(this.outerContext, existingWindowName);
        }

    }//MySQLWindowFunction

    private static class NoArgWindowFunction extends MySQLWindowFunction implements FunctionUtils.NoArgFunction {

        private NoArgWindowFunction(String name, TypeMeta returnType) {
            super(name, returnType);
        }

        @Override
        final void appendArg(StringBuilder sqlBuilder, _SqlContext context) {
            //no argument,no-op
        }

        @Override
        final void argToString(StringBuilder builder) {
            //no argument,no-op
        }

    }//NoArgWindowFunc


    private static class OneArgWindowFunction extends MySQLWindowFunction {

        private final ArmyExpression argument;

        private OneArgWindowFunction(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, returnType);
            this.argument = argument;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.argument.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            builder.append(this.argument);
        }


    }//OneArgWindowFunc


    private static class OneOptionArgWindowFunction extends MySQLWindowFunction {

        private final SQLWords option;

        private final ArmyExpression argument;


        private OneOptionArgWindowFunction(String name, @Nullable SQLWords option, ArmyExpression argument,
                                           TypeMeta returnType) {
            super(name, returnType);
            this.option = option;
            this.argument = argument;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final SQLWords option = this.option;
            if (option != null) {
                sqlBuilder.append(option.spaceRender());
            }
            this.argument.appendSql(sqlBuilder, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            final SQLWords option = this.option;
            if (option != null) {
                builder.append(_Constant.SPACE)
                        .append(option.spaceRender());
            }
            builder.append(this.argument);
        }


    }//OneOptionArgWindowFunction


    private static class MultiArgWindowFunction extends MySQLWindowFunction {

        private final SQLWords option;

        private final List<ArmyExpression> argList;

        private MultiArgWindowFunction(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
                                       TypeMeta returnType) {
            super(name, returnType);
            assert argList.size() > 0;
            this.option = option;
            this.argList = argList;
        }

        @Override
        final void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            FunctionUtils.appendArguments(this.option, this.argList, context);
        }

        @Override
        final void argToString(final StringBuilder builder) {
            FunctionUtils.argumentsToString(this.option, this.argList, builder);
        }


    }//MultiArgWindowFunction


    private static final class FromFirstLastMultiArgWindowFunc extends MultiArgWindowFunction
            implements MySQLWindowFunctions._FromFirstLastOverSpec {

        private FromFirstLast fromFirstLast;

        private NullTreatment nullTreatment;

        /**
         * @see #twoArgFromFirstWindowFunc(String, Expression, Expression, TypeMeta)
         */
        public FromFirstLastMultiArgWindowFunc(String name, List<ArmyExpression> argList, TypeMeta returnType) {
            super(name, null, argList, returnType);
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec fromFirst() {
            this.fromFirstLast = FromFirstLast.FROM_FIRST;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec fromLast() {
            this.fromFirstLast = FromFirstLast.FROM_LAST;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec ifFromFirst(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_FIRST : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._NullTreatmentOverSpec ifFromLast(BooleanSupplier predicate) {
            this.fromFirstLast = predicate.getAsBoolean() ? FromFirstLast.FROM_LAST : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec respectNulls() {
            this.nullTreatment = NullTreatment.RESPECT_NULLS;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ignoreNulls() {
            this.nullTreatment = NullTreatment.IGNORE_NULLS;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ifRespectNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.RESPECT_NULLS : null;
            return this;
        }

        @Override
        public MySQLWindowFunctions._OverSpec ifIgnoreNulls(BooleanSupplier predicate) {
            this.nullTreatment = predicate.getAsBoolean() ? NullTreatment.IGNORE_NULLS : null;
            return this;
        }

        @Override
        void appendClauseBeforeOver(final StringBuilder sqlBuilder, final _SqlContext context) {
            final FromFirstLast fromFirstLast = this.fromFirstLast;
            final NullTreatment nullTreatment = this.nullTreatment;

            if (fromFirstLast != null || nullTreatment != null) {
                if (fromFirstLast != null) {
                    sqlBuilder.append(fromFirstLast);
                }
                if (nullTreatment != null) {
                    sqlBuilder.append(nullTreatment);
                }
            }
        }

        @Override
        void outerClauseToString(final StringBuilder builder) {
            final FromFirstLast fromFirstLast = this.fromFirstLast;
            final NullTreatment nullTreatment = this.nullTreatment;

            if (fromFirstLast != null) {
                builder.append(fromFirstLast);
            }
            if (nullTreatment != null) {
                builder.append(nullTreatment);
            }
        }


    }//FromFirstLastMultiArgWindowFunc


    private static final class OneArgAggregateWindowFunc extends OneArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private OneArgAggregateWindowFunc(String name, ArmyExpression argument, TypeMeta returnType) {
            super(name, argument, returnType);
        }


    }//OneArgAggregateWindowFunc

    private static final class OneArgOptionAggregateWindowFunc extends OneOptionArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        /**
         * @see #oneArgAggregate(String, SQLWords, Expression, TypeMeta)
         */
        private OneArgOptionAggregateWindowFunc(String name, @Nullable SQLWords option, ArmyExpression argument,
                                                TypeMeta returnType) {
            super(name, option, argument, returnType);
        }


    }//OneArgAggregateWindowFunc


    private static final class MultiArgAggregateWindowFunc extends MultiArgWindowFunction
            implements MySQLWindowFunctions._ItemAggregateWindowFunc {

        private MultiArgAggregateWindowFunc(String name, @Nullable SQLWords option, List<ArmyExpression> argList,
                                            TypeMeta returnType) {
            super(name, option, argList, returnType);
        }

    }//MultiArgAggregateWindowFunc


    /**
     * @see #groupConcatFunc(SQLs.ArgDistinct, List, GroupConcatInnerClause)
     */
    private static final class GroupConcatFunction extends OperationExpression.SqlFunctionExpression {

        private final SQLs.ArgDistinct distinct;

        private final List<ArmyExpression> expList;

        private final GroupConcatInnerClause clause;

        private GroupConcatFunction(@Nullable SQLs.ArgDistinct distinct, List<ArmyExpression> expList
                , @Nullable GroupConcatInnerClause clause) {
            super("GROUP_CONCAT", StringType.INSTANCE);
            assert expList.size() > 0;
            this.distinct = distinct;
            this.expList = expList;
            this.clause = clause;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.distinct, this.expList, this.clause, this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof GroupConcatFunction) {
                final GroupConcatFunction o = (GroupConcatFunction) obj;
                match = o.name.equals(this.name)
                        && o.distinct == this.distinct
                        && o.expList.equals(this.expList)
                        && Objects.equals(o.clause, this.clause)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            if (this.distinct != null) {
                sqlBuilder.append(this.distinct.spaceRender());
            }
            FunctionUtils.appendArguments(this.distinct, this.expList, context);

            if (this.clause != null) {
                this.clause.appendSql(sqlBuilder, context);
            }
        }

        @Override
        void argToString(final StringBuilder builder) {
            if (this.distinct != null) {
                builder.append(this.distinct.spaceRender());
            }
            FunctionUtils.argumentsToString(this.distinct, this.expList, builder);

            if (this.clause != null) {
                builder.append(this.clause);
            }
        }


    }//GroupConcatFunction

    /**
     * @see #groupConcatClause()
     */
    static final class GroupConcatInnerClause
            extends OrderByClause.OrderByClauseClause<MySQLFunction._GroupConcatSeparatorClause, Item>
            implements MySQLFunction._GroupConcatOrderBySpec, ArmyFuncClause, _SelfDescribed {


        private String stringValue;

        private GroupConcatInnerClause() {
            super(ContextStack.peek());
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<? extends SortItem> sortItemList = this.orderByList();
            final int sortSize = sortItemList.size();


            if (sortSize > 0) {
                sqlBuilder.append(_Constant.SPACE_ORDER_BY);
                for (int i = 0; i < sortSize; i++) {
                    if (i > 0) {
                        sqlBuilder.append(_Constant.SPACE_COMMA);
                    }
                    ((_SelfDescribed) sortItemList.get(i)).appendSql(sqlBuilder, context);
                }
            }

            final String stringValue = this.stringValue;
            if (stringValue != null) {
                sqlBuilder.append(" SEPARATOR ");
                context.identifier(stringValue, sqlBuilder);
            }
        }

        @Override
        public Clause separator(final @Nullable String strVal) {
            this.endOrderByClauseIfNeed();
            if (this.stringValue != null) {
                throw ContextStack.criteriaError(this.context, "duplicate separator");
            } else if (strVal == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.stringValue = strVal;
            return this;
        }

        @Override
        public Clause separator(Supplier<String> supplier) {
            return this.separator(supplier.get());
        }

        @Override
        public Clause ifSeparator(Supplier<String> supplier) {
            this.endOrderByClauseIfNeed();
            this.stringValue = supplier.get();
            return this;
        }


    }//GroupConcatClause

    private static final class StatementDigestFunc extends OperationExpression.SqlFunctionExpression {


        private final PrimaryStatement statement;

        private final Visible visible;

        private final boolean literal;


        private StatementDigestFunc(String name, final PrimaryStatement statement, final Visible visible,
                                    final boolean literal, TypeMeta returnType) {
            super(name, returnType);
            this.statement = statement;
            this.visible = visible;
            this.literal = literal;
        }


        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {
            final PrimaryStatement statement = this.statement;

            final Stmt stmt;
            if (statement instanceof SelectStatement) {
                stmt = context.parser().select((Select) statement, false, this.visible);
            } else if (statement instanceof InsertStatement) {
                stmt = context.parser().insert((InsertStatement) statement, this.visible);
            } else if (statement instanceof UpdateStatement) {
                stmt = context.parser().update((UpdateStatement) statement, false, this.visible);
            } else if (statement instanceof DeleteStatement) {
                stmt = context.parser().delete((DeleteStatement) statement, false, this.visible);
            } else if (statement instanceof Values) {
                stmt = context.parser().values((Values) statement, this.visible);
            } else if (statement instanceof DqlStatement) {
                stmt = context.parser().dialectDql((DqlStatement) statement, this.visible);
            } else if (statement instanceof DmlStatement) {
                stmt = context.parser().dialectDml((DmlStatement) statement, this.visible);
            } else {
                //no bug,never here
                throw new IllegalArgumentException();
            }

            if (!(stmt instanceof SimpleStmt)) {
                String m = String.format("the argument of %s must be simple statement.", this.name);
                throw new CriteriaException(m);
            }

            if (this.literal) {
                context.appendLiteral(StringType.INSTANCE, ((SimpleStmt) stmt).sqlText());
            } else {
                context.appendParam(SingleParam.build(StringType.INSTANCE, ((SimpleStmt) stmt).sqlText()));
            }
        }

        @Override
        void argToString(final StringBuilder builder) {
            //TODO
        }


    }//StatementDigestFunc


    private enum JsonValueWord {
        NULL(" NULL"),
        ERROR(" ERROR"),
        ON_EMPTY(" ON EMPTY"),
        ON_ERROR(" ON ERROR");

        private final String spaceWords;

        JsonValueWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }


    }//NullOrError

    /**
     * @see JsonValueFunction#appendSql(StringBuilder, _SqlContext)
     */
    private static void appendOnEmptyOrErrorClause(final List<_Pair<Object, JsonValueWord>> actionList
            , final _SqlContext context) {
        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder();
        assert actionList.size() < 3;
        for (_Pair<Object, JsonValueWord> pair : actionList) {
            if (pair.first instanceof JsonValueWord) {
                assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                sqlBuilder.append(((JsonValueWord) pair.first).spaceWords);
            } else if (pair.first instanceof Expression) {
                sqlBuilder.append(_Constant.SPACE_DEFAULT);
                ((ArmyExpression) pair.first).appendSql(sqlBuilder, context);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
            sqlBuilder.append(pair.second.spaceWords);

        }//for
    }

    /**
     * @see JsonValueFunction#toString()
     */
    private static void onEmptyOrErrorClauseToString(final List<_Pair<Object, JsonValueWord>> actionList
            , final StringBuilder builder) {
        assert actionList.size() < 3;
        for (_Pair<Object, JsonValueWord> pair : actionList) {
            if (pair.first instanceof JsonValueWord) {
                assert pair.first == JsonValueWord.NULL || pair.first == JsonValueWord.ERROR;
                builder.append(((JsonValueWord) pair.first).spaceWords);
            } else if (pair.first instanceof Expression) {
                builder.append(_Constant.SPACE_DEFAULT)
                        .append(pair.first);
            } else {
                //no bug,never here
                throw new IllegalStateException();
            }
            assert pair.second == JsonValueWord.ON_EMPTY || pair.second == JsonValueWord.ON_ERROR;
            builder.append(pair.second.spaceWords);

        }//for
    }


    @SuppressWarnings("unchecked")
    private static abstract class OnEmptyOrErrorAction<S extends OnEmptyOrErrorAction<S>>
            implements MySQLFunction._OnEmptyOrErrorActionClause
            , MySQLFunction._OnErrorClause
            , MySQLFunction._OnEmptyClause {

        final CriteriaContext context;

        List<_Pair<Object, JsonValueWord>> actionList;

        private Object operateValue;

        private OnEmptyOrErrorAction(CriteriaContext context) {
            this.context = context;
        }


        @Override
        public final S nullWord() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.NULL;
            return (S) this;
        }

        @Override
        public final S error() {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = JsonValueWord.ERROR;
            return (S) this;
        }

        @Override
        public final S defaultValue(final Expression value) {
            if (this.operateValue != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (!(value instanceof ArmyExpression)) {
                throw ContextStack.nonArmyExp(this.context);
            }
            this.operateValue = value;
            return (S) this;
        }

        @Override
        public final S defaultValue(Supplier<Expression> supplier) {
            return this.defaultValue(supplier.get());
        }

        @Override
        public final <T> S defaultValue(Function<T, Expression> valueOperator, T value) {
            return this.defaultValue(valueOperator.apply(value));
        }

        @Override
        public final S defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName) {
            return this.defaultValue(valueOperator.apply(function.apply(keyName)));
        }

        @Override
        public final S onError() {
            final Object operateValue = this.operateValue;
            if (operateValue == null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.actionList;
            if (eventHandlerList == null) {
                this.actionList = Collections.singletonList(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else if (eventHandlerList.size() == 1) {
                eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_ERROR));
            } else {
                throw ContextStack.castCriteriaApi(this.context);
            }
            return (S) this;
        }

        @Override
        public final S onEmpty() {
            final Object operateValue = this.operateValue;
            if (operateValue == null || this.actionList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.operateValue = null;//clear
            final List<_Pair<Object, JsonValueWord>> eventHandlerList = new ArrayList<>(2);
            this.actionList = eventHandlerList;
            eventHandlerList.add(_Pair.create(operateValue, JsonValueWord.ON_EMPTY));
            return (S) this;
        }


    }//JsonTableOnEemptyOrErrorAction


    static final class JsonValueClause extends OnEmptyOrErrorAction<JsonValueClause>
            implements MySQLFunction._JsonValueReturningSpec
            , MySQLFunction._JsonValueOptionOnEmptySpec
            , MySQLFunction._JsonValueOnEmptySpec {

        private List<Object> returningList;

        private JsonValueClause() {
            super(ContextStack.peek());
        }


        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(final MySQLCastType type) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            this.returningList = Collections.singletonList(type);
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(final MySQLCastType type, Expression n) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            }
            if (!MySQLUtils.isSingleParamType(type)) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(4);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(Functions.FuncWord.RIGHT_PAREN);

            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n, SQLElement charset) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.CHAR) {
                throw typeError(type);
            } else if (!(charset instanceof MySQLCharset || charset instanceof SQLs.SQLIdentifierImpl)) {
                throw CriteriaUtils.funcArgError("JSON_VALUE", charset);
            }
            final List<Object> list = new ArrayList<>(5);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(n);
            list.add(Functions.FuncWord.RIGHT_PAREN);

            list.add(charset);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression m, Expression d) {
            if (this.returningList != null) {
                throw ContextStack.castCriteriaApi(this.context);
            } else if (type != MySQLCastType.DECIMAL) {
                throw typeError(type);
            }
            final List<Object> list = new ArrayList<>(6);

            list.add(type);
            list.add(Functions.FuncWord.LEFT_PAREN);
            list.add(m);
            list.add(Functions.FuncWord.COMMA);

            list.add(d);
            list.add(Functions.FuncWord.RIGHT_PAREN);
            this.returningList = list;
            return this;
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, int n) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, n));
        }

        @Override
        public MySQLFunction._JsonValueOptionOnEmptySpec returning(MySQLCastType type, int m, int d) {
            return this.returning(type, SQLs.literal(IntegerType.INSTANCE, m), SQLs.literal(IntegerType.INSTANCE, d));
        }


        private CriteriaException typeError(MySQLCastType type) {
            String m = String.format("%s error", type);
            return ContextStack.criteriaError(this.context, m);
        }


    }//JsonValueClause


    private static final class JsonValueFunction extends OperationExpression.SqlFunctionExpression implements MySQLFunction {

        private final ArmyExpression jsonDoc;

        private final ArmyExpression path;

        private final List<Object> returningList;

        private final List<_Pair<Object, JsonValueWord>> eventHandlerList;


        private JsonValueFunction(ArmyExpression jsonDoc, ArmyExpression path, JsonValueClause clause) {
            super("JSON_VALUE", StringType.INSTANCE);
            this.jsonDoc = jsonDoc;
            this.path = path;
            this.returningList = clause.returningList;
            this.eventHandlerList = clause.actionList;

        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name, this.jsonDoc, this.path, this.returningList, this.eventHandlerList,
                    this.returnType);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof JsonValueFunction) {
                final JsonValueFunction o = (JsonValueFunction) obj;
                match = o.name.equals(this.name)
                        && o.jsonDoc.equals(this.jsonDoc)
                        && Objects.equals(o.path, this.path)
                        && Objects.equals(o.returningList, this.returningList)
                        && Objects.equals(o.eventHandlerList, this.eventHandlerList)
                        && o.returnType.equals(this.returnType);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        void appendArg(final StringBuilder sqlBuilder, final _SqlContext context) {

            this.jsonDoc.appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_COMMA);
            this.path.appendSql(sqlBuilder, context);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        sqlBuilder.append(_Constant.SPACE_RETURNING)
                                .append(((MySQLCastType) o).spaceRender());
                    } else if (o == Functions.FuncWord.LEFT_PAREN) {
                        sqlBuilder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        sqlBuilder.append(((SQLWords) o).spaceRender());
                    } else if (o instanceof Expression) {
                        ((ArmyExpression) o).appendSql(sqlBuilder, context);
                    } else if (o instanceof SQLIdentifier) {
                        sqlBuilder.append(_Constant.SPACE);
                        context.identifier(((SQLIdentifier) o).render(), sqlBuilder);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                appendOnEmptyOrErrorClause(eventHandlerList, context);
            }//if

        }

        @Override
        void argToString(final StringBuilder builder) {
            builder.append(this.jsonDoc)
                    .append(_Constant.SPACE_COMMA)
                    .append(this.path);

            final List<Object> returningList = this.returningList;
            if (returningList != null) {
                assert returningList.get(0) instanceof MySQLCastType;
                for (Object o : returningList) {
                    if (o instanceof MySQLCastType) {
                        builder.append(_Constant.SPACE_RETURNING)
                                .append(((MySQLCastType) o).spaceRender());
                    } else if (o == Functions.FuncWord.LEFT_PAREN) {
                        builder.append(_Constant.LEFT_PAREN);
                    } else if (o instanceof SQLWords) {
                        builder.append(((SQLWords) o).spaceRender());
                    } else if (o instanceof Expression) {
                        builder.append(o);
                    } else if (o instanceof SQLIdentifier) {
                        builder.append(_Constant.SPACE);
                        builder.append(o);
                    } else {
                        //no bug,never here
                        throw new IllegalStateException();
                    }

                }//for
            }//if

            final List<_Pair<Object, JsonValueWord>> eventHandlerList = this.eventHandlerList;
            if (eventHandlerList != null) {
                onEmptyOrErrorClauseToString(eventHandlerList, builder);
            }//if
        }


    }//JsonValueFunction


    private interface JsonTableColumn extends _SelfDescribed {

    }


    private static final class ColumnEventClause implements MySQLFunction._JsonTableEmptyHandleClause,
            MySQLFunction._JsonTableOnEmptyClause, _SelfDescribed {

        private Object temp;

        private Object actionOnEmpty;

        private Object actionOnError;

        private ColumnEventClause() {
        }

        @Override
        public ColumnEventClause spaceNull() {
            this.temp = SQLs.NULL;
            return this;
        }

        @Override
        public ColumnEventClause spaceDefault(final @Nullable Object jsonExp) {
            if (jsonExp == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            if (jsonExp instanceof Expression) {
                this.temp = jsonExp;
            } else {
                this.temp = SQLs.literal(JsonType.TEXT, jsonExp);
            }
            return this;
        }

        @Override
        public ColumnEventClause spaceError() {
            this.temp = MySQLs.ERROR;
            return this;
        }

        @Override
        public Object onError() {
            final Object temp = this.temp;
            if (temp == null || this.actionOnError != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.temp = null;
            this.actionOnError = temp;
            return Collections.EMPTY_LIST;
        }

        @Override
        public ColumnEventClause onEmpty() {
            final Object temp = this.temp;
            if (temp == null || this.actionOnEmpty != null) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }
            this.temp = null;
            this.actionOnEmpty = temp;
            return this;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            Object action;

            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    action = this.actionOnEmpty;
                } else {
                    action = this.actionOnError;
                }

                if (action == null) {
                    continue;
                }

                if (action == SQLs.NULL) {
                    sqlBuilder.append(_Constant.SPACE_NULL);
                } else if (action == MySQLs.ERROR) {
                    sqlBuilder.append(MySQLs.ERROR.spaceRender());
                } else {
                    sqlBuilder.append(_Constant.SPACE_DEFAULT);
                    ((ArmyExpression) action).appendSql(sqlBuilder, context);
                }

                if (i == 0) {
                    sqlBuilder.append(" ON EMPTY");
                } else {
                    sqlBuilder.append(" ON ERROR");
                }


            } // for loop


        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            Object action;
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    action = this.actionOnEmpty;
                } else {
                    action = this.actionOnError;
                }

                if (action == null) {
                    continue;
                }

                if (action == SQLs.NULL) {
                    builder.append(_Constant.SPACE_NULL);
                } else if (action == MySQLs.ERROR) {
                    builder.append(MySQLs.ERROR.spaceRender());
                } else {
                    builder.append(_Constant.SPACE_DEFAULT)
                            .append(action);
                }

                if (i == 0) {
                    builder.append(" ON EMPTY");
                } else {
                    builder.append(" ON ERROR");
                }
            }

            return builder.toString();
        }


    } // ColumnEventClause


    private static final class JsonTableOrdinalityField extends FunctionField implements JsonTableColumn {

        private JsonTableOrdinalityField(String name) {
            super(name, UnsignedIntegerType.INSTANCE);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);
            context.identifier(this.name, sqlBuilder);
            sqlBuilder.append(MySQLs.FOR_ORDINALITY.spaceRender());
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(MySQLs.FOR_ORDINALITY.spaceRender())
                    .toString();
        }

    } // JsonTableOrdinalityField


    private static final class JsonTablePathField extends FunctionField implements JsonTableColumn {

        private final TypeItem typeItem;

        private final boolean exists;

        private final ArmyExpression pathExp;

        private final ColumnEventClause eventClause;

        private JsonTablePathField(String name, MappingType type, TypeItem typeItem, Expression pathExp,
                                   @Nullable ColumnEventClause eventClause) {
            super(name, type);
            this.typeItem = typeItem;
            this.exists = false;
            this.pathExp = (ArmyExpression) pathExp;
            this.eventClause = eventClause;
        }

        private JsonTablePathField(String name, MappingType type, TypeItem typeItem, Expression pathExp) {
            super(name, type);
            this.typeItem = typeItem;
            this.exists = true;
            this.pathExp = (ArmyExpression) pathExp;
            this.eventClause = null;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE);
            context.identifier(this.name, sqlBuilder);
            sqlBuilder.append(_Constant.SPACE);

            final TypeItem typeItem = this.typeItem;

            if (typeItem instanceof MappingType) {
                context.parser().typeName((MappingType) typeItem, sqlBuilder);
            } else if (typeItem instanceof MySQLType) {
                sqlBuilder.append(((MySQLType) typeItem).typeName());
            } else {
                ((_SelfDescribed) typeItem).appendSql(sqlBuilder, context);
            }

            if (this.exists) {
                sqlBuilder.append(MySQLs.EXISTS.spaceRender());
            }
            sqlBuilder.append(MySQLs.PATH.spaceRender());
            this.pathExp.appendSql(sqlBuilder, context);

            final ColumnEventClause eventClause = this.eventClause;
            if (eventClause != null) {
                eventClause.appendSql(sqlBuilder, context);
            }
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.SPACE)
                    .append(this.typeItem);

            if (this.exists) {
                builder.append(MySQLs.EXISTS.spaceRender());
            }

            builder.append(MySQLs.PATH.spaceRender())
                    .append(this.pathExp);

            final ColumnEventClause eventClause = this.eventClause;
            if (eventClause != null) {
                builder.append(eventClause);
            }
            return builder.toString();
        }


    } // JsonTablePathField


    static class MySQLJsonTableColumns implements MySQLFunction._JsonTableColumnSpaceClause,
            MySQLFunction._JsonTableColumnCommaClause,
            MySQLFunction._JsonTableColumnConsumerClause,
            _SelfDescribed {


        private List<FunctionField> fieldList;


        private MySQLJsonTableColumns() {
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(String name, SQLs.WordsForOrdinality forOrdinality) {
            return comma(name, forOrdinality);
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, path, pathExp);
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._JsonTableEmptyHandleClause> consumer) {
            return comma(name, type, path, pathExp, consumer);
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, exists, path, pathExp);
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, pathExp, columns, consumer);
        }

        @Override
        public final MySQLFunction._JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, pathExp, columns, space, consumer);
        }

        @Override
        public final MySQLJsonTableColumns comma(String name, SQLs.WordsForOrdinality forOrdinality) {
            return addField(new JsonTableOrdinalityField(name));
        }

        @Override
        public final MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordPath path, final Object pathExp) {
            return addPathField(name, type, false, pathExp, null);
        }

        @Override
        public final MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._JsonTableEmptyHandleClause> consumer) {
            final ColumnEventClause eventClause = new ColumnEventClause();
            CriteriaUtils.invokeConsumer(eventClause, consumer);

            return addPathField(name, type, false, pathExp, eventClause);
        }

        @Override
        public final MySQLJsonTableColumns comma(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return addPathField(name, type, true, pathExp, null);
        }

        @Override
        public final MySQLJsonTableColumns comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return null;
        }

        @Override
        public final MySQLJsonTableColumns comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return this;
        }


        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(String name, SQLs.WordsForOrdinality forOrdinality) {
            return comma(name, forOrdinality);
        }

        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, path, pathExp);
        }

        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<MySQLFunction._JsonTableEmptyHandleClause> consumer) {
            return comma(name, type, path, pathExp, consumer);
        }

        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp) {
            return comma(name, type, exists, path, pathExp);
        }

        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<MySQLFunction._JsonTableColumnSpaceClause> consumer) {
            return comma(nested, pathExp, columns, consumer);
        }

        @Override
        public final MySQLFunction._JsonTableColumnConsumerClause accept(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<MySQLFunction._JsonTableColumnConsumerClause> consumer) {
            return comma(nested, pathExp, columns, space, consumer);
        }

        @Override
        public void appendSql(StringBuilder sqlBuilder, _SqlContext context) {

        }


        private List<JsonTableColumn> endClause() {
            throw new UnsupportedOperationException();
        }


        private MySQLJsonTableColumns addPathField(final String name, final TypeItem typeItem, final boolean exists,
                                                   final Object pathExp, final @Nullable ColumnEventClause eventClause) {
            final Expression pathExpression;
            if (pathExp instanceof String) {
                pathExpression = SQLs.literal(StringType.INSTANCE, pathExp);
            } else if (pathExp instanceof Expression) {
                pathExpression = (Expression) pathExp;
            } else {
                String m = String.format("pathExp must be %s or %s", String.class.getName(), Expression.class.getName());
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            final MappingType type;
            if (typeItem instanceof MappingType) {
                type = (MappingType) typeItem;
            } else if (typeItem instanceof TypeDef) {
                type = mapType((TypeDef) typeItem);
            } else {
                String m = String.format("don't support %s[%s]", TypeItem.class, ClassUtils.safeClassName(typeItem));
                throw ContextStack.clearStackAndCriteriaError(m);
            }

            final JsonTablePathField field;
            if (exists) {
                assert eventClause == null;
                field = new JsonTablePathField(name, type, typeItem, pathExpression);
            } else {
                field = new JsonTablePathField(name, type, typeItem, pathExpression, eventClause);
            }
            return this.addField(field);
        }

        private MySQLJsonTableColumns addField(final FunctionField field) {
            List<FunctionField> fieldList = this.fieldList;
            if (fieldList == null) {
                this.fieldList = fieldList = _Collections.arrayList();
            } else if (!(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            fieldList.add(field);

            return this;
        }


    } // MySQLJsonTableColumns


    private static final class JsonTableFunc implements Functions._TabularFunction,
            _DerivedTable,
            _SelfDescribed {

        private final ArmyExpression exp;

        private final ArmyExpression pathExp;

        private final List<JsonTableColumn> columnList;

        /**
         * @param columnList unmodified list
         * @see #jsonTable(Object, Object, List)
         */
        private JsonTableFunc(Expression exp, Expression pathExp, List<JsonTableColumn> columnList) {
            this.exp = (ArmyExpression) exp;
            this.pathExp = (ArmyExpression) pathExp;
            this.columnList = columnList;
        }

        @Override
        public String name() {
            return "JSON_TABLE";
        }

        @Nullable
        @Override
        public Selection refSelection(String name) {
            return null;
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return null;
        }

        @Override
        public void appendSql(StringBuilder sqlBuilder, _SqlContext context) {

        }


    } // JsonTableFunc


}
