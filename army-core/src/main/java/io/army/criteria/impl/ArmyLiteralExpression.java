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
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;


/**
 * <p>
 * This class representing single-literal expression.
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @see ArmyParamExpression
 * @see ArmyRowLiteralExpression
 * @see ArmyRowParamExpression
 * @since 0.6.0
 */
abstract class ArmyLiteralExpression extends OperationExpression.OperationDefiniteExpression
        implements _LiteralExpression {

    /**
     * @see SQLs#literalValue(Object)
     */
    static ArmyLiteralExpression from(final @Nullable Object value, final boolean typeName) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final MappingType type;
        type = _MappingFactory.getDefaultIfMatch(value.getClass());
        if (type == null) {
            throw CriteriaUtils.clearStackAndNonDefaultType(value);
        }
        return new AnonymousLiteral(type, value, typeName);
    }


    /**
     * @throws CriteriaException throw when infer return codec {@link TableField}.
     * @see SQLs#literal(TypeInfer, Object)
     */
    static ArmyLiteralExpression single(final @Nullable TypeInfer infer, final @Nullable Object value, final boolean typeName) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingLiteral");
        }
        return new AnonymousLiteral(type, value, typeName);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedLiteral(TypeInfer, String)
     */
    static ArmyLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name, final boolean typeName) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedLiteral");
        }
        return new NamedNonNullLiteral(name, type, typeName);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedNullableLiteral(TypeInfer, String)
     */
    static ArmyLiteralExpression namedNullable(final @Nullable TypeInfer infer, final @Nullable String name, final boolean typeName) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedNullableLiteral");
        }
        return new NamedLiteral(name, type, typeName);
    }


    /**
     * @throws CriteriaException throw when infer isn't codec {@link TableField}.
     * @see SQLs#encodingLiteral(TypeInfer, Object)
     */
    static ArmyLiteralExpression encodingSingle(final @Nullable TypeInfer infer, final @Nullable Object value, final boolean typeName) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("literal");
        }
        return new AnonymousLiteral((TableField) infer, value, typeName);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedLiteral(TypeInfer, String)
     */
    static ArmyLiteralExpression encodingNamed(final @Nullable TypeInfer infer, final @Nullable String name, final boolean typeName) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedLiteral");
        }
        return new NamedNonNullLiteral(name, (TableField) infer, typeName);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedNullableLiteral(TypeInfer, String)
     */
    static ArmyLiteralExpression encodingNamedNullable(final @Nullable TypeInfer infer, final @Nullable String name, boolean typeName) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedNullableLiteral");
        }
        return new NamedLiteral(name, (TableField) infer, typeName);
    }

    static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for single-literal.");
    }

    final TypeMeta type;

    final boolean typeName;

    /**
     * private constructor
     */
    private ArmyLiteralExpression(TypeMeta type, boolean typeName) {
        if (type instanceof QualifiedField) {
            this.type = ((QualifiedField<?>) type).fieldMeta();
        } else {
            assert type instanceof FieldMeta || type instanceof MappingType;
            this.type = type;
        }
        this.typeName = typeName;

    }

    @Override
    public final TypeMeta typeMeta() {
        return this.type;
    }

    @Override
    public final boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
        // always false
        return false;
    }

    private static class AnonymousLiteral extends ArmyLiteralExpression
            implements SingleAnonymousValue {

        private final Object value;


        /**
         * @see #single(TypeInfer, Object, boolean)
         * @see #encodingSingle(TypeInfer, Object, boolean)
         */
        private AnonymousLiteral(TypeMeta type, @Nullable Object value, boolean typeName) {
            super(type, typeName);
            this.value = value;
        }

        @Override
        public final Object value() {
            return this.value;
        }

        @Override
        public void appendSqlWithoutType(StringBuilder sqlBuilder, _SqlContext context) {
            context.appendLiteral(this.type, this.value, false);
        }

        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            context.appendLiteral(this.type, this.value, this.typeName);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.type, this.value);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof AnonymousLiteral) {
                final AnonymousLiteral o = (AnonymousLiteral) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            final String s;
            if (this.value == null) {
                s = _Constant.SPACE_NULL;
            } else if (this.type instanceof TableField && ((TableField) this.type).codec()) {
                s = " {LITERAL}";
            } else {
                s = " " + this.value;
            }
            return s;
        }


    }//AnonymousLiteral


    private static class NamedLiteral extends ArmyLiteralExpression implements io.army.criteria.NamedLiteral {

        private final String name;


        private NamedLiteral(String name, TypeMeta type, boolean typeName) {
            super(type, typeName);
            this.name = name;
        }


        @Override
        public final String name() {
            return this.name;
        }


        @Override
        public void appendSqlWithoutType(StringBuilder sqlBuilder, _SqlContext context) {
            context.appendLiteral(this, false);
        }

        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            context.appendLiteral(this, this.typeName);
        }


        @Override
        public final String toString() {
            return " ?:" + this.name;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(this.type, this.name);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedLiteral) {
                final NamedLiteral o = (NamedLiteral) obj;
                match = (o instanceof NamedNonNullLiteral == this instanceof NamedNonNullLiteral)
                        && o.name.equals(this.name)
                        && o.type.equals(this.type);
            } else {
                match = false;
            }
            return match;
        }


    }//NamedLiteral


    private static final class NamedNonNullLiteral extends NamedLiteral
            implements SqlValueParam.NonNullValue {

        private NamedNonNullLiteral(String name, TypeMeta type, boolean typeName) {
            super(name, type, typeName);
        }


    }//NamedNonNullLiteral


}
