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
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.SingleParam;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * <p>
 * This class representing single-value parameter expression.
 * * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * * @see ArmyLiteralExpression
 *
 * @see ArmyRowParamExpression
 * @see ArmyRowLiteralExpression
 * @since 0.6.0
 */
abstract class ArmyParamExpression extends OperationExpression.OperationDefiniteExpression
        implements ParamExpression {

    /**
     * @see SQLs#paramValue(Object)
     */
    static ArmyParamExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final MappingType type;
        type = _MappingFactory.getDefaultIfMatch(value.getClass());
        if (type == null) {
            throw CriteriaUtils.clearStackAndNonDefaultType(value);
        }
        return new AnonymousParam(type, value);
    }


    /**
     * @throws CriteriaException throw when infer return codec {@link TableField}.
     * @see SQLs#param(TypeInfer, Object)
     */
    static ArmyParamExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingParam");
        }
        return new AnonymousParam(type, value);
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedParam(TypeInfer, String)
     */
    static ArmyParamExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedParam");
        }
        return new ImmutableNamedNonNullParam(name, type);
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedNullableParam(TypeInfer, String)
     */
    static ArmyParamExpression namedNullable(@Nullable TypeInfer infer, @Nullable String name) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedNullableParam");
        }
        return new ImmutableNamedParam(name, type);
    }


    /**
     * @throws CriteriaException throw when infer isn't codec {@link TableField}.
     * @see SQLs#encodingParam(TypeInfer, Object)
     */
    static ArmyParamExpression encodingSingle(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("param");
        }
        return new AnonymousParam((TableField) infer, value);
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedParam(TypeInfer, String)
     */
    static ArmyParamExpression encodingNamed(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedParam");
        }
        return new ImmutableNamedNonNullParam(name, (TableField) infer);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedNullableParam(TypeInfer, String)
     */
    static ArmyParamExpression encodingNamedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedNullableParam");
        }
        return new ImmutableNamedParam(name, (TableField) infer);
    }


    static CriteriaException typeInferReturnCodecField(String methodName) {
        String m = String.format("infer return codec field,you should invoke %s.%s(TypeInfer,Object)",
                SQLs.class.getName(), methodName);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static CriteriaException typeInferIsNotCodecField(String methodName) {
        String m = String.format("infer isn't codec field,you should invoke %s.%s(TypeInfer,Object)",
                SQLs.class.getName(), methodName);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    private static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for single-parameter.");
    }

    /**
     * private constructor
     */
    private ArmyParamExpression() {
    }

    @Override
    public final void appendSql(StringBuilder sqlBuilder, _SqlContext context) {
        context.appendParam(this);
    }


    private static final class AnonymousParam extends ArmyParamExpression
            implements SingleParam, SingleAnonymousValue {

        private final TypeMeta type;

        private final Object value;


        /**
         * @see #single(TypeInfer, Object)
         */
        private AnonymousParam(TypeMeta type, @Nullable Object value) {
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.value = value;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public Object value() {
            return this.value;
        }

        @Override
        public String toString() {
            return " ?";
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof AnonymousParam) {
                final AnonymousParam o = (AnonymousParam) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }


    }//AnonymousParam


    private static class ImmutableNamedParam extends ArmyParamExpression implements NamedParam.NamedSingle {

        private final String name;
        private final TypeMeta type;


        /**
         * @see #namedNullable(TypeInfer, String)
         * @see #encodingNamedNullable(TypeInfer, String)
         */
        private ImmutableNamedParam(String name, TypeMeta type) {
            this.name = name;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public final String name() {
            return this.name;
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
            } else if (obj instanceof ImmutableNamedParam) {
                final ImmutableNamedParam o = (ImmutableNamedParam) obj;
                match = (o instanceof ImmutableNamedNonNullParam == this instanceof ImmutableNamedNonNullParam)
                        && o.name.equals(this.name)
                        && o.type.equals(this.type);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableNamedParam


    private static final class ImmutableNamedNonNullParam extends ImmutableNamedParam
            implements SqlValueParam.NonNullValue {

        /**
         * @see #named(TypeInfer, String)
         * @see #encodingNamed(TypeInfer, String)
         */
        private ImmutableNamedNonNullParam(String name, TypeMeta type) {
            super(name, type);
        }


    }//ImmutableNamedNonNullParam


}
