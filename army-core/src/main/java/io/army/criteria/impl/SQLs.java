package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ItemPair;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SingleParam;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 * </p>
 */
@SuppressWarnings({"unused"})
public abstract class SQLs extends StandardSyntax {


    /**
     * private constructor
     */
    private SQLs() {
    }


    public static final class Modifier implements SQLWords {

        private final String words;

        /**
         * private constructor
         */
        private Modifier(String words) {
            this.words = words;
        }

        @Override
        public String render() {
            return this.words;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(SQLs.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(SQLs.Modifier.class.getSimpleName())
                    .append(_Constant.POINT)
                    .append(this.words)
                    .toString();
        }

    }//Modifier

    public static final Modifier ALL = new Modifier("ALL");

    public static final Modifier DISTINCT = new Modifier("DISTINCT");


    public static StandardInsert._PrimaryOptionSpec<Void> primaryInsert() {
        return StandardInserts.primaryInsert(null);
    }

    public static <C> StandardInsert._PrimaryOptionSpec<C> primaryInsert(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardInserts.primaryInsert(criteria);
    }

    public static Update._StandardDomainUpdateClause<Void> domainUpdate() {
        return StandardUpdate.simpleDomain(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update._StandardDomainUpdateClause<C> domainUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simpleDomain(criteria);
    }

    public static Update._StandardSingleUpdateClause<Void> singleUpdate() {
        return StandardUpdate.simpleSingle(null);
    }

    /**
     * @param criteria a object instance, map or bean
     * @param <C>      criteria java type used to create dynamic update and sub query
     */
    public static <C> Update._StandardSingleUpdateClause<C> singleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.simpleSingle(criteria);
    }


    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @see #namedMultiParams(DataField, int)
     */
    public static Update._StandardBatchDomainUpdateClause<Void> batchDomainUpdate() {
        return StandardUpdate.batchDomain(null);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     */
    public static <C> Update._StandardBatchDomainUpdateClause<C> batchDomainUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batchDomain(criteria);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @see #namedMultiParams(DataField, int)
     */
    public static Update._StandardBatchSingleUpdateClause<Void> batchSingleUpdate() {
        return StandardUpdate.batchSingle(null);
    }

    /**
     * <p>
     * Batch domain update
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     * @see #namedMultiParams(DataField, int)
     */
    public static <C> Update._StandardBatchSingleUpdateClause<C> batchSingleUpdate(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardUpdate.batchSingle(criteria);
    }

    public static Delete.StandardDeleteSpec<Void> domainDelete() {
        return StandardDelete.simple(null);
    }

    public static <C> Delete.StandardDeleteSpec<C> domainDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.simple(criteria);
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     */
    public static Delete.StandardBatchDeleteSpec<Void> batchDomainDelete() {
        return StandardDelete.batch(null);
    }

    /**
     * <p>
     * Batch domain delete
     * </p>
     *
     * @param criteria a criteria object , map or bean
     * @param <C>      criteria java type used to create dynamic batch update and sub query
     */
    public static <C> Delete.StandardBatchDeleteSpec<C> batchDomainDelete(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardDelete.batch(criteria);
    }

    public static StandardQuery._StandardSelectClause<Void, Select> query() {
        return StandardSimpleQuery.query(null);
    }


    public static <C> StandardQuery._StandardSelectClause<C, Select> query(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.query(criteria);
    }

    public static StandardQuery._StandardSelectClause<Void, SubQuery> subQuery() {
        return StandardSimpleQuery.subQuery(null);
    }

    public static <C> StandardQuery._StandardSelectClause<C, SubQuery> subQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.subQuery(criteria);
    }


    public static StandardQuery._StandardSelectClause<Void, ScalarExpression> scalarSubQuery() {
        return StandardSimpleQuery.scalarSubQuery(null);
    }


    public static <C> StandardQuery._StandardSelectClause<C, ScalarExpression> scalarSubQuery(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardSimpleQuery.scalarSubQuery(criteria);
    }


    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    @Deprecated
    static Expression _nonNullParam(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = ParamExpression.single((TableField) type, value);
        } else {
            resultExpression = ParamExpression.single(type.typeMeta(), value);
        }
        return resultExpression;
    }


    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     * @see #_funcLiteral(Object)
     * @see #_funcParamList(TypeMeta, Object)
     */
    @Deprecated
    static ArmyExpression _funcParam(final @Nullable Object value) {
        final ArmyExpression expression;
        if (value == null) {
            expression = SQLs._nullParam();
        } else if (value instanceof Expression) {
            expression = (ArmyExpression) value;
        } else {
            expression = (ArmyExpression) param(value);
        }
        return expression;
    }

    @Deprecated
    static ArmyExpression _funcParam(final TypeMeta typeMeta, final @Nullable Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see #_funcParam(Object)
     * @see #_funcLiteral(Object)
     */
    @Deprecated
    static ArmyExpression _funcParamList(final TypeMeta typeMeta, final @Nullable Object value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static ArmyExpression _funcLiteral(final @Nullable Object value) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    static ArmyExpression _funcLiteral(final TypeMeta typeMeta, final @Nullable Object value) {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     */
    static ArmyExpression _nullParam() {
        return NullParam.INSTANCE;
    }

    /**
     * package method that is used by army developer.
     *
     * @param value {@link Expression} or parameter
     */
    @Deprecated
    static Expression _nullableParam(final Expression type, final @Nullable Object value) {
        final Expression expression;
        if (value instanceof Expression) {
            expression = (Expression) value;
        } else if (type instanceof TableField) {
            expression = ParamExpression.single((TableField) type, value);
        } else {
            expression = ParamExpression.single(type.typeMeta(), value);
        }
        return expression;
    }

    @Nullable
    static Object _safeParam(final @Nullable Object value) {
        return value instanceof Supplier ? ((Supplier<?>) value).get() : value;
    }


    /**
     * package method that is used by army developer.
     *
     * @param exp {@link Expression} or parameter
     */
    @Deprecated
    static ArmyExpression _nonNullExp(final @Nullable Object exp) {
        final Expression expression;
        if (exp == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (exp instanceof Expression) {
            if (!(exp instanceof ArmyExpression)) {
                throw CriteriaContextStack.nonArmyExp(CriteriaContextStack.peek());
            }
            expression = (Expression) exp;
        } else {
            expression = SQLs.param(exp);
        }
        return (ArmyExpression) expression;
    }

    /**
     * package method that is used by army developer.
     */
    @Deprecated
    static Expression _nonNullLiteral(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), _Exceptions::expressionIsNull);
        } else if (value instanceof Expression) {
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = LiteralExpression.single((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.single(type.typeMeta(), value);
        }
        return resultExpression;
    }

    /**
     * package method that is used by army developer.
     */
    @Deprecated
    static ArmyExpression _nullableLiteral(final Expression type, final @Nullable Object value) {
        final Expression resultExpression;
        if (value == null) {
            resultExpression = SQLs.nullWord();
        } else if (value instanceof Expression) {
            //maybe jvm don't correctly recognize overload method of io.army.criteria.Expression
            resultExpression = (Expression) value;
        } else if (type instanceof TableField) {
            resultExpression = LiteralExpression.single((TableField) type, value);
        } else {
            resultExpression = LiteralExpression.single(type.typeMeta(), value);
        }
        return (ArmyExpression) resultExpression;
    }


    /**
     * <p>
     * Value must be below types:
     *     <ul>
     *         <li>{@link Boolean}</li>
     *         <li>{@link String}</li>
     *         <li>{@link Integer}</li>
     *         <li>{@link Long}</li>
     *         <li>{@link Short}</li>
     *         <li>{@link Byte}</li>
     *         <li>{@link Double}</li>
     *         <li>{@link Float}</li>
     *         <li>{@link java.math.BigDecimal}</li>
     *         <li>{@link java.math.BigInteger}</li>
     *         <li>{@code  byte[]}</li>
     *         <li>{@link BitSet}</li>
     *         <li>{@link io.army.struct.CodeEnum}</li>
     *         <li>{@link io.army.struct.TextEnum}</li>
     *         <li>{@link java.time.LocalTime}</li>
     *         <li>{@link java.time.LocalDate}</li>
     *         <li>{@link java.time.LocalDateTime}</li>
     *         <li>{@link java.time.OffsetDateTime}</li>
     *         <li>{@link java.time.ZonedDateTime}</li>
     *         <li>{@link java.time.OffsetTime}</li>
     *         <li>{@link java.time.ZoneId}</li>
     *         <li>{@link java.time.Month}</li>
     *         <li>{@link java.time.DayOfWeek}</li>
     *         <li>{@link java.time.Year}</li>
     *         <li>{@link java.time.YearMonth}</li>
     *         <li>{@link java.time.MonthDay}</li>
     *     </ul>
     * </p>
     *
     * @param value non null
     * @return parameter expression
     * @see #literal(Object)
     */
    public static Expression param(final Object value) {
        return ParamExpression.single(_MappingFactory.getDefault(value.getClass()), value);
    }

    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param type  non-nul
     * @param value nullable
     * @see #param(MappingType, Object)
     * @see #param(TypeInfer, Object)
     * @see #literal(MappingType, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression param(final MappingType type, final @Nullable Object value) {
        return ParamExpression.single(type, value);
    }

    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(MappingType, Object)
     * @see #param(TypeInfer, Object)
     * @see #literal(MappingType, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression param(final TypeInfer typeExp, final @Nullable Object value) {
        final Expression result;
        if (typeExp instanceof TableField) {  //for field codec
            if (value instanceof Supplier) {
                result = ParamExpression.single((TableField) typeExp, ((Supplier<?>) value).get());
            } else {
                result = ParamExpression.single((TableField) typeExp, value);
            }
        } else if (value instanceof Supplier) {
            result = ParamExpression.single(typeExp.typeMeta(), ((Supplier<?>) value).get());
        } else {
            result = ParamExpression.single(typeExp.typeMeta(), value);
        }
        return result;
    }


    /**
     * <p>
     * Create multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @see #multiParams(MappingType, Collection)
     * @see #multiParams(TypeInfer, Collection)
     * @see #multiLiterals(MappingType, Collection)
     * @see #multiLiterals(TypeInfer, Collection)
     */
    public static Expression multiParams(final MappingType type, final Collection<?> values) {
        return ParamExpression.multi(type, values);
    }

    /**
     * <p>
     * Create multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     *
     * @param typeExp non-null,the type of element of values.
     * @param values  non-null and non-empty
     * @see #multiParams(MappingType, Collection)
     * @see #multiParams(TypeInfer, Collection)
     * @see #multiLiterals(MappingType, Collection)
     * @see #multiLiterals(TypeInfer, Collection)
     */
    public static Expression multiParams(final TypeInfer typeExp, final Collection<?> values) {
        final Expression result;
        if (typeExp instanceof TableField) {  //for field codec
            result = ParamExpression.multi((TableField) typeExp, values);
        } else {
            result = ParamExpression.multi(typeExp.typeMeta(), values);
        }
        return result;
    }




    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedParam(final MappingType type, final String name) {
        return ParamExpression.namedSingle(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedParam(final TypeInfer typeExp, final String name) {
        final Expression result;
        if (typeExp instanceof TableField) {
            result = ParamExpression.namedSingle((TableField) typeExp, name);
        } else {
            result = ParamExpression.namedSingle(typeExp.typeMeta(), name);
        }
        return result;
    }


    /**
     * <p>
     * Create named nullable parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableParam(MappingType type, String name) {
        return ParamExpression.namedNullableSingle(type, name);
    }

    /**
     * <p>
     * Create named nullable parameter expression for batch update(or delete) and values(assignment) insert
     * </p>
     *
     * @return named nullable parameter expression
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableParam(final TypeInfer typeExp, final String name) {
        final Expression result;
        if (typeExp instanceof TableField) {
            result = ParamExpression.namedNullableSingle((TableField) typeExp, name);
        } else {
            result = ParamExpression.namedNullableSingle(typeExp.typeMeta(), name);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiParams(final MappingType type, final String name, final int size) {
        return ParamExpression.namedMulti(type, name, size);
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param typeExp non-null,the type of element of {@link Collection}
     * @param name    non-null,the key name of {@link Map} or the field name of java bean.
     * @param size    positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiParams(final TypeInfer typeExp, final String name, final int size) {
        final Expression result;
        if (typeExp instanceof TableField) {
            //for field codec
            result = ParamExpression.namedMulti((TableField) typeExp, name, size);
        } else {
            result = ParamExpression.namedMulti(typeExp.typeMeta(), name, size);
        }
        return result;
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param field non-null,field as the type of element of {@link Collection}
     *              ,{@link  DataField#fieldName()} as the key name of {@link Map} or the field name of java bean.
     * @param size  positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiParams(final DataField field, final int size) {
        final Expression result;
        if (field instanceof TableField) {
            //for field codec
            result = ParamExpression.namedMulti((TableField) field, field.fieldName(), size);
        } else {
            result = ParamExpression.namedMulti(field.typeMeta(), field.fieldName(), size);
        }
        return result;
    }

    /**
     * <p>
     * Value must be below types:
     *     <ul>
     *         <li>{@link Boolean}</li>
     *         <li>{@link String}</li>
     *         <li>{@link Integer}</li>
     *         <li>{@link Long}</li>
     *         <li>{@link Short}</li>
     *         <li>{@link Byte}</li>
     *         <li>{@link Double}</li>
     *         <li>{@link Float}</li>
     *         <li>{@link java.math.BigDecimal}</li>
     *         <li>{@link java.math.BigInteger}</li>
     *         <li>{@code  byte[]}</li>
     *         <li>{@link BitSet}</li>
     *         <li>{@link io.army.struct.CodeEnum}</li>
     *         <li>{@link io.army.struct.TextEnum}</li>
     *         <li>{@link java.time.LocalTime}</li>
     *         <li>{@link java.time.LocalDate}</li>
     *         <li>{@link java.time.LocalDateTime}</li>
     *         <li>{@link java.time.OffsetDateTime}</li>
     *         <li>{@link java.time.ZonedDateTime}</li>
     *         <li>{@link java.time.OffsetTime}</li>
     *         <li>{@link java.time.ZoneId}</li>
     *         <li>{@link java.time.Month}</li>
     *         <li>{@link java.time.DayOfWeek}</li>
     *         <li>{@link java.time.Year}</li>
     *         <li>{@link java.time.YearMonth}</li>
     *         <li>{@link java.time.MonthDay}</li>
     *     </ul>
     * </p>
     *
     * @param value non null
     * @return literal expression
     * @see #param(Object)
     */
    public static Expression literal(final Object value) {
        return LiteralExpression.single(_MappingFactory.getDefault(value.getClass()), value);
    }

    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     * </p>
     *
     * @param type  non-null
     * @param value nullable
     * @see #param(MappingType, Object)
     * @see #param(TypeInfer, Object)
     * @see #literal(MappingType, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression literal(final MappingType type, final @Nullable Object value) {
        return LiteralExpression.single(type, value);
    }

    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     * </p>
     *
     * @param typeExp non-null
     * @param value   nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(MappingType, Object)
     * @see #param(TypeInfer, Object)
     * @see #literal(MappingType, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static Expression literal(final TypeInfer typeExp, final @Nullable Object value) {
        final Expression result;
        if (typeExp instanceof TableField) {
            if (value instanceof Supplier) {
                result = LiteralExpression.single((TableField) typeExp, ((Supplier<?>) value).get());
            } else {
                result = LiteralExpression.single((TableField) typeExp, value);
            }
        } else if (value instanceof Supplier) {
            result = LiteralExpression.single(typeExp.typeMeta(), ((Supplier<?>) value).get());
        } else {
            result = LiteralExpression.single(typeExp.typeMeta(), value);
        }
        return result;
    }

    /**
     * <p>
     * Create multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @see #multiParams(MappingType, Collection)
     * @see #multiParams(TypeInfer, Collection)
     * @see #multiLiterals(MappingType, Collection)
     * @see #multiLiterals(TypeInfer, Collection)
     */
    public static Expression multiLiterals(final MappingType type, final Collection<?> values) {
        return LiteralExpression.multi(type, values);
    }

    /**
     * <p>
     * Create multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     *
     * @param typeExp non-null,the type of element of values.
     * @param values  non-null and non-empty
     * @see #multiParams(MappingType, Collection)
     * @see #multiParams(TypeInfer, Collection)
     * @see #multiLiterals(MappingType, Collection)
     * @see #multiLiterals(TypeInfer, Collection)
     */
    public static Expression multiLiterals(final TypeInfer typeExp, final Collection<?> values) {
        final Expression result;
        if (typeExp instanceof TableField) {
            result = LiteralExpression.multi((TableField) typeExp, values);
        } else {
            result = LiteralExpression.multi(typeExp.typeMeta(), values);
        }
        return result;
    }



    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null
     * @param name non-null
     * @return non-null named literal expression
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedLiteral(final MappingType type, final String name) {
        return LiteralExpression.namedSingle(type, name);
    }

    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param typeExp non-null
     * @param name    non-null
     * @return non-null named literal expression
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedLiteral(final TypeInfer typeExp, final String name) {
        final Expression result;
        if (typeExp instanceof TableField) {
            result = LiteralExpression.namedSingle((TableField) typeExp, name);
        } else {
            result = LiteralExpression.namedSingle(typeExp.typeMeta(), name);
        }
        return result;
    }


    /**
     * <p>
     * Create named nullable literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null
     * @param name non-null
     * @return named nullable literal expression
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableLiteral(final MappingType type, final String name) {
        return LiteralExpression.namedNullableSingle(type, name);
    }

    /**
     * <p>
     * Create named nullable literal expression. This expression can only be used in values insert statement.
     * </p>
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param typeExp non-null
     * @param name    non-null
     * @return named nullable literal expression
     * @see #namedParam(MappingType, String)
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(MappingType, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(MappingType, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(MappingType, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     */
    public static Expression namedNullableLiteral(final TypeInfer typeExp, final String name) {
        final Expression result;
        if (typeExp instanceof TableField) {
            result = LiteralExpression.namedNullableSingle((TableField) typeExp, name);
        } else {
            result = LiteralExpression.namedNullableSingle(typeExp.typeMeta(), name);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size non-null,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiLiterals(MappingType type, String name, int size) {
        return LiteralExpression.namedMulti(type, name, size);
    }

    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param typeExp non-null,the type of element of {@link Collection}
     * @param name    non-null,the key name of {@link Map} or the field name of java bean.
     * @param size    positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiLiterals(final TypeInfer typeExp, final String name, final int size) {
        final Expression result;
        if (typeExp instanceof TableField) {
            //for field codec
            result = LiteralExpression.namedMulti((TableField) typeExp, name, size);
        } else {
            result = LiteralExpression.namedMulti(typeExp.typeMeta(), name, size);
        }
        return result;
    }

    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param field non-null,field as the type of element of {@link Collection}
     *              ,{@link  DataField#fieldName()} as the key name of {@link Map} or the field name of java bean.
     * @param size  positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedMultiParams(MappingType, String, int)
     * @see #namedMultiParams(TypeInfer, String, int)
     * @see #namedMultiParams(DataField, int)
     * @see #namedMultiLiterals(MappingType, String, int)
     * @see #namedMultiLiterals(TypeInfer, String, int)
     * @see #namedMultiLiterals(DataField, int)
     */
    public static Expression namedMultiLiterals(final DataField field, final int size) {
        final Expression result;
        if (field instanceof TableField) {
            //for field codec
            result = LiteralExpression.namedMulti((TableField) field, field.fieldName(), size);
        } else {
            result = LiteralExpression.namedMulti(field.typeMeta(), field.fieldName(), size);
        }
        return result;
    }


    static Expression star() {
        return StarLiteral.INSTANCE;
    }


    public static ItemPair itemPair(final DataField field, final @Nullable Object value) {
        return SQLs._itemPair(field, null, value);
    }


    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     *
     * @param value {@link Expression} or parameter.
     * @see Update._SetClause#setPairs(BiConsumer)
     * @see Update._SetClause#setPairs(Consumer)
     */
    static ArmyItemPair _itemPair(final DataField field, final @Nullable AssignOperator operator
            , final @Nullable Object value) {
        if (operator != null && value == null) {
            throw _Exceptions.expressionIsNull();
        }
        if (field instanceof TableField) {
            final TableField f = (TableField) field;
            if (f.updateMode() == UpdateMode.IMMUTABLE) {
                throw _Exceptions.immutableField(field);
            }
            final String fieldName = field.fieldName();
            if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                throw _Exceptions.armyManageField(f);
            }

            if (!f.nullable()
                    && (value == null
                    || (value instanceof Expression && ((ArmyExpression) value).isNullValue()))) {
                throw _Exceptions.nonNullField(f);
            }
        }

        final Expression valueExp;
        if (value instanceof Expression) {
            valueExp = (Expression) value;
        } else {
            valueExp = SQLs.param(field, value);
        }
        final ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new FieldItemPair(field, (ArmyExpression) valueExp);
        } else {
            itemPair = new OperatorItemPair(field, operator, (ArmyExpression) valueExp);
        }
        return itemPair;
    }

    /**
     * <p>
     * package method that is used by army developer.
     * </p>
     */
    static _ItemPair _itemExpPair(final DataField field, @Nullable Expression value) {
        assert value != null;
        return SQLs._itemPair(field, null, value);
    }

    public static ItemPair itemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
        return new RowItemPair(fieldList, subQuery);
    }

    public static ItemPair plusEqual(final DataField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value);
    }

    public static ItemPair minusEqual(final DataField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value);
    }


    public static ExpressionPair expPair(final Expression first, final Expression second) {
        return new ExpressionPairImpl(first, second);
    }


    /**
     * @return DEFAULT expression that output key word {@code DEFAULT}.
     */
    public static Expression defaultWord() {
        return SQLs.DefaultWord.INSTANCE;
    }


    /**
     * @return NULL expression that output key word {@code NULL}.
     */
    public static Expression nullWord() {
        return NullWord.INSTANCE;
    }

    public static Expression trueWord() {
        return BooleanWord.TRUE;
    }

    public static Expression falseWord() {
        return BooleanWord.FALSE;
    }








    /*################################## blow sql reference method ##################################*/

    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return CriteriaContextStack.peek().qualifiedField(tableAlias, field);
    }

    public static DerivedField ref(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().ref(derivedTable, derivedFieldName);
    }

    public static DerivedField outerRef(String derivedTable, String derivedFieldName) {
        return CriteriaContextStack.peek().outerRef(derivedTable, derivedFieldName);
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement after selection list end,eg: ORDER BY clause.
     * </p>
     */
    public static Expression ref(String selectionAlias) {
        return CriteriaContextStack.peek().ref(selectionAlias);
    }

    /**
     * <p>
     * Reference session variable.
     * </p>
     *
     * @throws CriteriaException when var not exists
     */
    public static VarExpression var(String varName) {
        return CriteriaContextStack.root().var(varName);
    }


    /**
     * <p>
     * Create session variable.
     * </p>
     *
     * @throws CriteriaException when var exists.
     */
    public static VarExpression createVar(String varName, TypeMeta paramMeta)
            throws CriteriaException {
        return CriteriaContextStack.root().createVar(varName, paramMeta);
    }

    public static CteItem refCte(String cteName) {
        return CriteriaContextStack.peek().refCte(cteName);
    }

    public static StandardQuery._StandardNestedLeftParenClause<Void> nestedItems() {
        return StandardNestedItems.create(null);
    }

    public static <C> StandardQuery._StandardNestedLeftParenClause<C> nestedItems(C criteria) {
        Objects.requireNonNull(criteria);
        return StandardNestedItems.create(criteria);
    }

    public static StandardQuery._IfOnClause<Void> block(TableMeta<?> table, String tableAlias) {
        return DynamicBlock.standard(null, table, tableAlias);
    }

    public static <C> StandardQuery._IfOnClause<C> block(C criteria, TableMeta<?> table, String tableAlias) {
        Objects.requireNonNull(criteria);
        return DynamicBlock.standard(criteria, table, tableAlias);
    }


    public static StandardQuery._IfOnClause<Void> block(SubQuery subQuery, String tableAlias) {
        return DynamicBlock.standard(null, subQuery, tableAlias);
    }

    public static <C> StandardQuery._IfOnClause<C> block(C criteria, SubQuery subQuery, String tableAlias) {
        Objects.requireNonNull(criteria);
        return DynamicBlock.standard(null, subQuery, tableAlias);
    }

    public static <T> SelectionGroup group(TableMeta<T> table, String alias) {
        return SelectionGroups.singleGroup(table, alias);
    }

    public static <T> SelectionGroup group(String tableAlias, List<FieldMeta<T>> fieldList) {
        return SelectionGroups.singleGroup(tableAlias, fieldList);
    }

    /**
     * @return a group that no {@link ParentTableMeta#id()} column
     */
    public static <T> SelectionGroup groupWithoutId(TableMeta<T> table, String alias) {
        return SelectionGroups.groupWithoutId(table, alias);
    }

    public static <T> SelectionGroup childGroup(ChildTableMeta<T> child, String childAlias
            , String parentAlias) {
        return SelectionGroups.childGroup(child, childAlias, parentAlias);
    }


    public static SelectionGroup derivedGroup(String alias) {
        return SelectionGroups.derivedGroup(alias);
    }

    public static SelectionGroup derivedGroup(String alias, List<String> derivedFieldNameList) {
        if (derivedFieldNameList.size() == 0) {
            throw new CriteriaException("derivedFieldNameList must not empty");
        }
        return SelectionGroups.derivedGroup(alias, derivedFieldNameList);
    }

    public static _Cte cte(String name, SubStatement subStatement) {
        return new CteImpl(name, subStatement);
    }

    public static _Cte cte(String name, List<String> aliasLst, SubStatement subStatement) {
        return new CteImpl(name, aliasLst, subStatement);
    }



    /*################################## blow sql key word operate method ##################################*/

    /**
     * @param subQuery non-null
     */
    public static IPredicate exists(SubQuery subQuery) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.EXISTS, subQuery);
    }

    /**
     * @param subQuery non-null
     */
    public static IPredicate notExists(SubQuery subQuery) {
        return UnaryPredicate.fromSubQuery(UnaryOperator.NOT_EXISTS, subQuery);
    }



    /*-------------------below Aggregate Function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count() {
        return SQLFunctions.oneArgFunc("COUNT", SQLs.star(), LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static Expression count(Expression expr) {
        return SQLFunctions.oneArgFunc("COUNT", expr, LongType.INSTANCE);
    }


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * </p>
     *
     * @param <E> The java type The expression thant reference kwy word {@code DEFAULT}
     */
    static final class DefaultWord<E> extends NonOperationExpression {

        private static final DefaultWord<?> INSTANCE = new DefaultWord<>();

        private DefaultWord() {
        }


        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_DEFAULT);
        }

        @Override
        public String toString() {
            return _Constant.SPACE_DEFAULT;
        }

    }// DefaultWord


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     */
    static final class NullWord extends NonOperationExpression implements SqlValueParam.SingleNonNamedValue {

        private static final NullWord INSTANCE = new NullWord();


        private NullWord() {
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_NULL);
        }

        @Override
        public TypeMeta typeMeta() {
            return _NullType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _Constant.SPACE_NULL;
        }


    }// NullWord


    private static final class StarLiteral extends NonOperationExpression {

        private static final StarLiteral INSTANCE = new StarLiteral();

        private StarLiteral() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation();
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.sqlBuilder().append(" *");
        }


    }//StarLiteral

    static final class BooleanWord extends OperationExpression {

        private static final BooleanWord TRUE = new BooleanWord(true);

        private static final BooleanWord FALSE = new BooleanWord(false);

        private final boolean value;

        private BooleanWord(boolean value) {
            this.value = value;
        }

        @Override
        public TypeMeta typeMeta() {
            return BooleanType.INSTANCE;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE);
            if (this.value) {
                sqlBuilder.append(BooleanType.TRUE);
            } else {
                sqlBuilder.append(BooleanType.FALSE);
            }
        }

        @Override
        public String toString() {
            final String s;
            if (this.value) {
                s = _Constant.SPACE + BooleanType.TRUE;
            } else {
                s = _Constant.SPACE + BooleanType.FALSE;
            }
            return s;
        }


    }//BooleanWord


    static abstract class ArmyItemPair implements _ItemPair {

        final SqlOperand right;

        private ArmyItemPair(SqlOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #itemPair(DataField, Object)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final DataField field;

        private FieldItemPair(DataField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final _SetClauseContext context) {
            final DataField field = this.field;
            //1. append left item
            context.appendSetLeftItem(field);
            //2. append operator
            if (this instanceof OperatorItemPair) {
                ((OperatorItemPair) this).operator
                        .appendOperator(context.parser().dialectMode(), field, context);
            } else {
                context.sqlBuilder()
                        .append(_Constant.SPACE_EQUAL);
            }
            //3. append right item
            ((_Expression) this.right).appendSql(context);
        }

        @Override
        public final DataField field() {
            return this.field;
        }

        @Override
        public final _Expression value() {
            return (_Expression) this.right;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.field);
            if (this instanceof OperatorItemPair) {
                builder.append(((OperatorItemPair) this).operator);
            } else {
                builder.append(_Constant.SPACE_EQUAL);
            }
            builder.append(this.right);
            return builder.toString();
        }

    }//FieldItemPair

    private static final class OperatorItemPair extends FieldItemPair {

        final AssignOperator operator;

        private OperatorItemPair(DataField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<DataField> fieldList;

        private RowItemPair(List<? extends DataField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = CriteriaUtils.selectionCount(subQuery);
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<DataField> tempList = new ArrayList<>(fieldList.size());
            for (DataField field : fieldList) {
                if (!(field instanceof TableField)) {
                    tempList.add(field);
                    continue;
                }
                if (((TableField) field).updateMode() == UpdateMode.IMMUTABLE) {
                    throw _Exceptions.immutableField(field);
                }
                final String fieldName = field.fieldName();
                if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                    throw _Exceptions.armyManageField((TableField) field);
                }
                tempList.add(field);
            }
            this.fieldList = Collections.unmodifiableList(tempList);
        }

        @Override
        public void appendItemPair(final _SetClauseContext context) {
            final List<? extends DataField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendSetLeftItem(fieldList.get(i));
            }
            //3. append right paren
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            sqlBuilder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            context.parser().rowSet((SubQuery) this.right, context);

        }

        @Override
        public List<? extends DataField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends DataField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(fieldList.get(i));
            }
            //3. append right paren
            builder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            builder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            builder.append(this.right);
            return builder.toString();
        }

    }//RowItemPair

    private static final class NullParam extends NonOperationExpression
            implements SingleParam, SqlValueParam.SingleValue {

        private static final NullParam INSTANCE = new NullParam();

        private NullParam() {
        }

        @Override
        public TypeMeta typeMeta() {
            return _NullType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }


    }//StringTypeNull

    /**
     * @see #expPair(Expression, Expression)
     */
    static final class ExpressionPairImpl implements ExpressionPair {

        final Expression first;

        final Expression second;

        private ExpressionPairImpl(@Nullable Expression first, @Nullable Expression second) {
            assert first != null;
            assert second != null;
            this.first = first;
            this.second = second;
        }
    }//ExpressionPairImpl


    static final class CteImpl implements _Cte {

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        CteImpl(String name, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = Collections.emptyList();
            this.subStatement = subStatement;
        }


        CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = columnNameList;
            this.subStatement = subStatement;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<String> columnNameList() {
            return this.columnNameList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<? extends SelectItem> selectItemList() {
            final SubStatement subStatement = this.subStatement;
            final List<? extends SelectItem> list;
            if (subStatement instanceof DerivedTable) {
                list = ((DerivedTable) subStatement).selectItemList();
            } else if (subStatement instanceof _Statement._ReturningListSpec) {
                list = ((_Statement._ReturningListSpec) subStatement).returningList();
            } else {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public Selection selection(final String derivedFieldName) {
            final SubStatement subStatement = this.subStatement;
            final Selection selection;
            if (subStatement instanceof DerivedTable) {
                selection = ((DerivedTable) subStatement).selection(derivedFieldName);
            } else {
                selection = null;
            }
            return selection;
        }


    }//CteImpl


}
