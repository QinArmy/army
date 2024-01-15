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
import io.army.mapping.MappingType;
import io.army.mapping.NoCastIntegerType;
import io.army.mapping.NoCastTextType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * Package class,this class is base class of {@link SQLs}.
 *
 * @see SQLs
 * @since 0.6.0
 */
abstract class SQLSyntax extends Functions {


    /**
     * package constructor
     */
    SQLSyntax() {
    }

    /**
     * <p>Get default {@link MappingType} of javaType,if not found,throw {@link CriteriaException}
     *
     * @return non-null
     * @throws CriteriaException throw when not found default {@link MappingType} of javaType
     */
    public static MappingType mappingTypeOf(final Class<?> javaType) {
        final MappingType type;
        type = _MappingFactory.getDefaultIfMatch(javaType);
        if (type == null) {
            String m = String.format("Not found default %s of %s", MappingType.class.getName(), javaType.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }
        return type;
    }

    /**
     * <p>Get default {@link MappingType} of javaType,if not found,return null
     *
     * @return nullable
     */
    @Nullable
    public static MappingType getMappingTypeOf(final Class<?> javaType) {
        return _MappingFactory.getDefaultIfMatch(javaType);
    }


    /**
     * <p>
     * Value must be below types:
     * <ul>
     *     <li>{@link Boolean}</li>
     *     <li>{@link String}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Byte}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link java.math.BigDecimal}</li>
     *     <li>{@link java.math.BigInteger}</li>
     *     <li>{@code  byte[]}</li>
     *     <li>{@link BitSet}</li>
     *     <li>{@link io.army.struct.CodeEnum}</li>
     *     <li>{@link io.army.struct.TextEnum}</li>
     *     <li>{@link java.time.LocalTime}</li>
     *     <li>{@link java.time.LocalDate}</li>
     *     <li>{@link java.time.LocalDateTime}</li>
     *     <li>{@link java.time.OffsetDateTime}</li>
     *     <li>{@link java.time.ZonedDateTime}</li>
     *     <li>{@link java.time.OffsetTime}</li>
     *     <li>{@link java.time.ZoneId}</li>
     *     <li>{@link java.time.Month}</li>
     *     <li>{@link java.time.DayOfWeek}</li>
     *     <li>{@link java.time.Year}</li>
     *     <li>{@link java.time.YearMonth}</li>
     *     <li>{@link java.time.MonthDay}</li>
     * </ul>
     *
     * @param value non null
     * @return parameter expression
     * @see #literalValue(Object)
     */
    public static ParamExpression paramValue(final Object value) {
        return ArmyParamExpression.from(value);
    }


    /**
     * <p>
     * Create parameter expression, parameter expression output parameter placeholder({@code ?})
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @throws io.army.criteria.CriteriaException throw when infer is codec {@link FieldMeta}.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static ParamExpression param(final TypeInfer type, final @Nullable Object value) {
        final ParamExpression result;
        if (value instanceof Supplier) {
            result = ArmyParamExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = ArmyParamExpression.single(type, value);
        }
        return result;
    }

    /**
     * <p>
     * Create encoding parameter expression, parameter expression output parameter placeholder({@code ?})
     *
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will be invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static ParamExpression encodingParam(final TypeInfer type, final @Nullable Object value) {
        final ParamExpression result;
        if (value instanceof Supplier) {
            result = ArmyParamExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = ArmyParamExpression.encodingSingle(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
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
    public static ParamExpression namedParam(final TypeInfer type, final String name) {
        return ArmyParamExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
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
    public static ParamExpression encodingNamedParam(final TypeInfer type, final String name) {
        return ArmyParamExpression.encodingNamed(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
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
    public static ParamExpression namedNullableParam(final TypeInfer type, final String name) {
        return ArmyParamExpression.namedNullable(type, name);
    }

    /**
     * <p>
     * Create named non-null parameter expression for batch update(delete) and values insert.
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
    public static ParamExpression encodingNamedNullableParam(final TypeInfer type, final String name) {
        return ArmyParamExpression.encodingNamedNullable(type, name);
    }


    /**
     * <p>
     * Value must be below types:
     * <ul>
     *     <li>{@link Boolean}</li>
     *     <li>{@link String}</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Byte}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link java.math.BigDecimal}</li>
     *     <li>{@link java.math.BigInteger}</li>
     *     <li>{@code  byte[]}</li>
     *     <li>{@link BitSet}</li>
     *     <li>{@link io.army.struct.CodeEnum}</li>
     *     <li>{@link io.army.struct.TextEnum}</li>
     *     <li>{@link java.time.LocalTime}</li>
     *     <li>{@link java.time.LocalDate}</li>
     *     <li>{@link java.time.LocalDateTime}</li>
     *     <li>{@link java.time.OffsetDateTime}</li>
     *     <li>{@link java.time.ZonedDateTime}</li>
     *     <li>{@link java.time.OffsetTime}</li>
     *     <li>{@link java.time.ZoneId}</li>
     *     <li>{@link java.time.Month}</li>
     *     <li>{@link java.time.DayOfWeek}</li>
     *     <li>{@link java.time.Year}</li>
     *     <li>{@link java.time.YearMonth}</li>
     *     <li>{@link java.time.MonthDay}</li>
     * </ul>
     *
     * @param value non null
     * @return literal expression
     * @see SQLs#paramValue(Object)
     * @see SQLs#space(Object)
     */
    public static LiteralExpression literalValue(final Object value) {
        return ArmyLiteralExpression.from(value);
    }

    /**
     * <p>
     * Create literal expression with nonNullValue.
     * This method is similar to {@link SQLs#literalValue(Object)},except that two exceptions :
     * <ul>
     *     <li>{@link String} map to {@link NoCastTextType} not {@link io.army.mapping.StringType}</li>
     *     <li>{@link Integer} map to {@link NoCastIntegerType} not {@link io.army.mapping.IntegerType}</li>
     * </ul>
     *
     * @param nonNullValue non-null value
     * @see SQLs#literalValue(Object)
     */
    public static LiteralExpression space(final Object nonNullValue) {
        final LiteralExpression expression;
        if (nonNullValue instanceof String) {
            expression = ArmyLiteralExpression.single(NoCastTextType.INSTANCE, nonNullValue);
        } else if (nonNullValue instanceof Integer) {
            expression = ArmyLiteralExpression.single(NoCastIntegerType.INSTANCE, nonNullValue);
        } else {
            expression = ArmyLiteralExpression.from(nonNullValue);
        }
        return expression;
    }


    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     *
     * @param type  non-null
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static LiteralExpression literal(final TypeInfer type, final @Nullable Object value) {
        final LiteralExpression result;
        if (value instanceof Supplier) {
            result = ArmyLiteralExpression.single(type, ((Supplier<?>) value).get());
        } else {
            result = ArmyLiteralExpression.single(type, value);
        }
        return result;
    }

    /**
     * <p>
     * Create literal expression,literal expression will output literal of value
     *
     * @param type  non-null
     * @param value nullable,if value is instance of {@link Supplier},then {@link Supplier#get()} will invoked.
     * @see #param(TypeInfer, Object)
     * @see #literal(TypeInfer, Object)
     */
    public static LiteralExpression encodingLiteral(final TypeInfer type, final @Nullable Object value) {
        final LiteralExpression result;
        if (value instanceof Supplier) {
            result = ArmyLiteralExpression.encodingSingle(type, ((Supplier<?>) value).get());
        } else {
            result = ArmyLiteralExpression.encodingSingle(type, value);
        }
        return result;
    }


    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     *
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
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
    public static LiteralExpression namedLiteral(final TypeInfer type, final String name) {
        return ArmyLiteralExpression.named(type, name);
    }

    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     *
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
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
    public static LiteralExpression encodingNamedLiteral(final TypeInfer type, final String name) {
        return ArmyLiteralExpression.encodingNamed(type, name);
    }

    /**
     * <p>
     * Create named non-null literal expression. This expression can only be used in values insert statement.
     *
     * <p>
     * Note: this method couldn't be used in batch update(delete) statement.
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
    public static LiteralExpression namedNullableLiteral(final TypeInfer type, final String name) {
        return ArmyLiteralExpression.namedNullable(type, name);
    }

    /**
     * <p>Create named non-null literal expression. This expression can only be used in values insert statement.
     *
     * <p>Note: this method couldn't be used in batch update(delete) statement.
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
    public static LiteralExpression encodingNamedNullableLiteral(final TypeInfer type, final String name) {
        return ArmyLiteralExpression.encodingNamedNullable(type, name);
    }

    /**
     * <p>Create multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see #rowLiteral(TypeInfer, Collection)
     */
    public static RowParamExpression rowParam(final TypeInfer type, final Collection<?> values) {
        return ArmyRowParamExpression.multi(type, values);
    }

    /**
     * <p>
     * Create multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @see #rowParam(TypeInfer, Collection)
     */
    public static RowLiteralExpression rowLiteral(final TypeInfer type, final Collection<?> values) {
        return ArmyRowLiteralExpression.multi(type, values);
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     *
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
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
    public static RowParamExpression namedRowParam(final TypeInfer type, final String name, final int size) {
        return ArmyRowParamExpression.named(type, name, size);
    }

    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     *
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
     *
     * @param type non-null,the type of element of {@link Collection}
     * @param name non-null,the key name of {@link Map} or the field name of java bean.
     * @param size positive,the size of {@link Collection}
     * @return named non-null multi literal expression
     * @see #namedRowParam(TypeInfer, String, int)
     */
    public static RowLiteralExpression namedRowLiteral(final TypeInfer type, final String name, final int size) {
        return ArmyRowLiteralExpression.named(type, name, size);
    }

    /**
     * <p>
     * Create multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? , ? , ? ...
     * but as right operand of  IN(or NOT IN) operator, will output (  ? , ? , ? ... )
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingRowLiteral(TypeInfer, Collection)
     */
    public static RowParamExpression encodingRowParam(final TypeInfer type, final Collection<?> values) {
        return ArmyRowParamExpression.encodingMulti(type, values);
    }

    /**
     * <p>
     * Create multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL , LITERAL , LITERAL ...
     * but as right operand of  IN(or NOT IN) operator, will output (  LITERAL , LITERAL , LITERAL ... )
     *
     * @param type   non-null,the type of element of values.
     * @param values non-null and non-empty
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see #encodingRowParam(TypeInfer, Collection)
     */
    public static RowLiteralExpression encodingRowLiteral(final TypeInfer type, final Collection<?> values) {
        return ArmyRowLiteralExpression.encodingMulti(type, values);
    }

    /**
     * <p>
     * Create named non-null multi parameter expression, multi parameter expression will output multi parameter placeholders like below:
     * ? [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output (  ? [, ...] )
     *
     * <p>
     * Named multi parameter expression is used in batch update(or delete) and values insert.
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
    public static RowParamExpression encodingNamedRowParam(final TypeInfer type, final String name, final int size) {
        return ArmyRowParamExpression.encodingNamed(type, name, size);
    }


    /**
     * <p>
     * Create named non-null multi literal expression, multi literal expression will output multi LITERAL like below:
     * LITERAL [, ...]
     * but as the right operand of  IN(or NOT IN) operator, will output ( LITERAL  [, ...] )
     *
     * <p>
     * This expression can only be used in values insert statement,this method couldn't be used in batch update(delete) statement.
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
    public static RowLiteralExpression encodingNamedRowLiteral(final TypeInfer type, final String name, final int size) {
        return ArmyRowLiteralExpression.encodingNamed(type, name, size);
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
     *
     * @throws CriteriaException throw when<ul>
     *                           <li>current statement don't support this method,eg: single-table UPDATE statement</li>
     *                           <li>qualified field don't exists,here always is deferred,because army validate qualified field when statement end.</li>
     *                           </ul>
     */
    public static <T> QualifiedField<T> field(String tableAlias, FieldMeta<T> field) {
        final QualifiedField<T> qualifiedField;
        qualifiedField = ContextStack.peek().field(tableAlias, field);
        if (qualifiedField == null) {
            throw CriteriaContexts.unknownQualifiedField(tableAlias, field);
        }
        return qualifiedField;
    }

    /**
     * <p>
     * Reference a derived field from current statement.
     *
     * @param derivedAlias   derived table alias,
     * @param selectionAlias derived field alias
     * @throws CriteriaException            throw when current statement don't support derived field (eg: single-table UPDATE statement).
     * @throws UnknownDerivedFieldException throw when derived filed is unknown.
     */
    public static DerivedField refField(String derivedAlias, String selectionAlias) {
        final DerivedField field;
        field = ContextStack.peek().refField(derivedAlias, selectionAlias);
        if (field == null) {
            throw CriteriaContexts.unknownDerivedField(derivedAlias, selectionAlias);
        }
        return field;
    }


    /**
     * <p>
     * Reference a {@link  Selection} of current statement ,eg: ORDER BY clause.
     * The {@link Expression} returned don't support {@link Expression#as(String)} method.
     *
     * <p>
     * <strong>NOTE</strong> : override,if selection alias duplication.
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

    public static ItemPair plusEqual(final SqlField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.PLUS_EQUAL, value);
    }

    public static ItemPair minusEqual(final SqlField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.MINUS_EQUAL, value);
    }

    public static ItemPair timesEqual(final SqlField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.TIMES_EQUAL, value);
    }

    public static ItemPair divideEqual(final SqlField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.DIVIDE_EQUAL, value);
    }

    public static ItemPair modeEqual(final SqlField field, final Expression value) {
        return SQLs._itemPair(field, AssignOperator.MODE_EQUAL, value);
    }


//    public static <I extends Item, R extends Expression> SQLFunction._CaseFuncWhenClause<R> Case(
//            Function<_ItemExpression<I>, R> endFunc, Function<TypeInfer, I> asFunc) {
//        return FunctionUtils.caseFunction(null, endFunc, asFunc);
//    }




    /*-------------------below package method -------------------*/



    /*-------------------below private method-------------------*/


}
