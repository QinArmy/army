package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * Package class,this class is base class of {@link SQLs}.
 * </p>
 *
 * @see SQLs
 * @since 1.0
 */
abstract class SQLsSyntax extends Functions {


    /**
     * package constructor
     */
    SQLsSyntax() {
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
     * @see #literalValue(Object)
     */
    public static SimpleExpression paramValue(final Object value) {
        return ParamExpression.from(value);
    }


    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @throws io.army.criteria.CriteriaException throw when infer is codec {@link FieldMeta}.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression param(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = ParamExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = ParamExpression.single(type, value);
        }
        return result;
    }

    /**
     * <p>
     * Create encoding parameter expression, parameter expression output parameter placeholder({@code ?})
     * </p>
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression encodingParam(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = ParamExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = ParamExpression.encodingSingle(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedParam(final TypeInfer type, final String name) {
        return ParamExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedParam(final TypeInfer type, final String name) {
        return ParamExpression.encodingNamed(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedNullableParam(final TypeInfer type, final String name) {
        return ParamExpression.namedNullable(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
     * </p>
     *
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedNullableParam(final TypeInfer type, final String name) {
        return ParamExpression.encodingNamedNullable(type, name);
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
     * @see #paramValue(Object)
     */
    public static SimpleExpression literalValue(final Object value) {
        return LiteralExpression.from(value);
    }


    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     * </p>
     *
     * @param type  non-null
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression literal(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = LiteralExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = LiteralExpression.single(type, value);
        }
        return result;
    }

    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     * </p>
     *
     * @param type  non-null
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static SimpleExpression encodingLiteral(final TypeInfer type, final @Nullable Object value) {
        final SimpleExpression result;
        if (value instanceof Supplier) {
            result = LiteralExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = LiteralExpression.encodingSingle(type, value);
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
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedLiteral(final TypeInfer type, final String name) {
        return LiteralExpression.named(type, name);
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
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedLiteral(final TypeInfer type, final String name) {
        return LiteralExpression.encodingNamed(type, name);
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
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>infer is codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     * @see #encodingNamedNullableLiteral(TypeInfer, String)
     */
    public static SimpleExpression namedNullableLiteral(final TypeInfer type, final String name) {
        return LiteralExpression.namedNullable(type, name);
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
     * @param name non-null and non-empty
     * @return non-null named literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see #namedParam(TypeInfer, String)
     * @see #namedNullableParam(TypeInfer, String)
     * @see #namedLiteral(TypeInfer, String)
     * @see #namedNullableLiteral(TypeInfer, String)
     * @see #encodingNamedParam(TypeInfer, String)
     * @see #encodingNamedNullableParam(TypeInfer, String)
     * @see #encodingNamedLiteral(TypeInfer, String)
     */
    public static SimpleExpression encodingNamedNullableLiteral(final TypeInfer type, final String name) {
        return LiteralExpression.encodingNamedNullable(type, name);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see #rowParam(TypeInfer, Collection)
     * @see #rowLiteral(TypeInfer, Collection)
     */
    public static RowExpression rowParam(final TypeInfer type, final Collection<?> values) {
        return ParamRowExpression.multi(type, values);
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
     * @see #rowParam(TypeInfer, Collection)
     */
    public static RowExpression rowLiteral(final TypeInfer type, final Collection<?> values) {
        return LiteralRowExpression.multi(type, values);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see #namedRowLiteral(TypeInfer, String, int)
     */
    public static RowExpression namedRowParam(final TypeInfer type, final String name, final int size) {
        return ParamRowExpression.named(type, name, size);
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
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedRowParam(TypeInfer, String, int)
     */
    public static RowExpression namedRowLiteral(final TypeInfer type, final String name, final int size) {
        return LiteralRowExpression.named(type, name, size);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingRowLiteral(TypeInfer, Collection)
     */
    public static RowExpression encodingRowParam(final TypeInfer type, final Collection<?> values) {
        return ParamRowExpression.encodingMulti(type, values);
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
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingRowParam(TypeInfer, Collection)
     */
    public static RowExpression encodingRowLiteral(final TypeInfer type, final Collection<?> values) {
        return LiteralRowExpression.encodingMulti(type, values);
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output (  ? [, ...] )
     * </p>
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi parameter expression
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingNamedRowLiteral(TypeInfer, String, int)
     */
    public static RowExpression encodingNamedRowParam(final TypeInfer type, final String name, final int size) {
        return ParamRowExpression.encodingNamed(type, name, size);
    }


    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output ( LITERAL  [, ...] )
     * </p>
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     * </p>
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingNamedRowParam(TypeInfer, String, int)
     */
    public static RowExpression encodingNamedRowLiteral(final TypeInfer type, final String name, final int size) {
        return LiteralRowExpression.encodingNamed(type, name, size);
    }

    public static RowExpression row(SubQuery subQuery) {
        return RowExpressions.row(subQuery);
    }

    public static RowExpression row(Supplier<SubQuery> subQuery) {
        return RowExpressions.row(subQuery.get());
    }

    public static RowExpression row(Object element) {
        return RowExpressions.row(element);
    }

    public static RowExpression row(Object element1, Object element2) {
        return RowExpressions.row(element1, element2);
    }

    public static RowExpression row(Object element1, Object element2, Object element3, Object... restElement) {
        return RowExpressions.row(element1, element2, element3, restElement);
    }

    public static RowExpression row(Consumer<Consumer<Object>> consumer) {
        return RowExpressions.row(consumer);
    }

    public static RowElement space(String derivedAlias, SQLs.SymbolPeriod period,
                                   SQLs.SymbolAsterisk asterisk) {
        return ContextStack.peek().row(derivedAlias, period, asterisk);
    }

    public static RowElement space(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table) {
        return ContextStack.peek().row(tableAlias, period, table); // register derived row
    }


    /**
     * <p>
     * Get a {@link QualifiedField}. You don't need a {@link QualifiedField},if no self-join in statement.
     * </p>
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>current statement don't support this method,eg: single-table UPDATE statement</li>
     *                           <li>qualified field don't exists,here always is deferred,because army validate qualified field when statement end.</li>
     *                           </ul>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        return ContextStack.peek().field(tableAlias, field);
    }

    /**
     * <p>
     * Reference a derived field from current statement.
     * </p>
     *
     * @param derivedAlias   derived table alias,
     * @param selectionAlias derived field alias
     */
    public static DerivedField refThis(String derivedAlias, String selectionAlias) {
        return ContextStack.peek().refThis(derivedAlias, selectionAlias, false);
    }

    /**
     * <p>
     * Reference a derived field from outer statement.
     * </p>
     *
     * @param derivedAlias   derived table alias,
     * @param selectionAlias derived field alias
     */
    public static DerivedField refOuter(String derivedAlias, String selectionAlias) {
        return ContextStack.peek().refOuter(derivedAlias, selectionAlias);
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement ,eg: ORDER BY clause.
     * The {@link Expression} returned don't support {@link Expression#as(String)} method.
     * </p>
     * <p>
     * <strong>NOTE</strong> : override,if selection alias duplication.
     * </p>
     *
     * @return the {@link Expression#typeMeta()} of the {@link Expression} returned always return {@link TypeMeta#mappingType()} of {@link Selection#typeMeta()} .
     * @throws CriteriaException then when <ul>
     *                           <li>current statement don't support this method,eg: UPDATE statement</li>
     *                           <li>the {@link Selection} not exists,here possibly is deferred,if you invoke this method before SELECT clause end. eg: postgre DISTINCT ON clause</li>
     *                           </ul>
     */
    public static Expression refSelection(String selectionAlias) {
        return ContextStack.peek().refSelection(selectionAlias);
    }

    /**
     * <p>
     * Reference a {@link  Selection} of current statement ,eg: ORDER BY clause.
     * The {@link Expression} returned don't support {@link Expression#as(String)} method.
     * </p>
     *
     * @param selectionOrdinal based 1 .
     * @return the {@link Expression#typeMeta()} of the {@link Expression} returned always return {@link io.army.mapping.IntegerType#INSTANCE}
     * @throws CriteriaException throw when<ul>
     *                           <li>selectionOrdinal less than 1</li>
     *                           <li>the {@link Selection} not exists,here possibly is deferred,if you invoke this method before SELECT clause end. eg: postgre DISTINCT ON clause</li>
     *                           <li>current statement don't support this method,eg: UPDATE statement</li>
     *                           </ul>
     */
    public static Expression refSelection(int selectionOrdinal) {
        return ContextStack.peek().refSelection(selectionOrdinal);
    }


    public static Expression parens(Expression expression) {
        return OperationExpression.bracketExp(expression);
    }

    public static SimplePredicate bracket(IPredicate predicate) {
        return OperationPredicate.bracketPredicate(predicate);
    }

    public static SimpleExpression bitwiseNot(Expression exp) {
        return Expressions.unaryExp(UnaryExpOperator.BITWISE_NOT, exp);
    }

    public static SimpleExpression negate(Expression exp) {
        return Expressions.unaryExp(UnaryExpOperator.NEGATE, exp);
    }

    public static IPredicate not(IPredicate predicate) {
        return OperationPredicate.notPredicate(predicate);
    }



    /*################################## blow sql key word operate method ##################################*/

    /**
     * @param subQuery non-null
     */
    public static IPredicate exists(SubQuery subQuery) {
        return Expressions.existsPredicate(false, subQuery);
    }

    /**
     * @param subQuery non-null
     */
    public static IPredicate notExists(SubQuery subQuery) {
        return Expressions.existsPredicate(true, subQuery);
    }

    public static ItemPair plusEqual(final SQLField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value);
    }

    public static ItemPair minusEqual(final SQLField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value);
    }


//    public static <I extends Item, R extends Expression> SQLFunction._CaseFuncWhenClause<R> Case(
//            Function<_ItemExpression<I>, R> endFunc, Function<TypeInfer, I> asFunc) {
//        return FunctionUtils.caseFunction(null, endFunc, asFunc);
//    }




    /*-------------------below package method -------------------*/



    /*-------------------below private method-------------------*/


}
