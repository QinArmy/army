package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.TypeMeta;
import io.army.stmt.SingleParam;
import io.army.util._StringUtils;

import java.util.Objects;

/**
 * <p>
 * This class representing single-value parameter expression.
 * </p>
 *
 * @see MultiParamExpression
 * @since 1.0
 */
abstract class SingleParamExpression extends OperationExpression.OperationSimpleExpression
        implements SqlValueParam.SingleValue, SQLParam, ArmyExpression {

    /**
     * @see SQLs#paramValue(Object)
     */
    static SingleParamExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        try {
            return new ImmutableAnonymousSingleParam(_MappingFactory.getDefault(value.getClass()), value);
        } catch (MetaException e) {
            throw ContextStack.clearStackAndCriteriaError(e.getMessage());
        }
    }


    /**
     * @throws CriteriaException throw when infer return codec {@link TableField}.
     * @see SQLs#param(TypeInfer, Object)
     */
    static SingleParamExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        final TypeMeta type;
        final AnonymousSingleParam expression;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayAnonymousSingleParam((DelayTypeInfer) infer, value);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingParam");
        } else {
            expression = new ImmutableAnonymousSingleParam(type, value);
        }
        return expression;
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedParam(TypeInfer, String)
     */
    static SingleParamExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        final TypeMeta type;
        final NamedSingleParam expression;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedNonNullSingleParam((DelayTypeInfer) infer, name);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedParam");
        } else {
            expression = new ImmutableNamedNonNullSingleParam(type, name);
        }
        return expression;
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedNullableParam(TypeInfer, String)
     */
    static SingleParamExpression namedNullable(@Nullable TypeInfer infer, @Nullable String name) {
        final TypeMeta type;
        final NamedSingleParam expression;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedSingleParam((DelayTypeInfer) infer, name);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedNullableParam");
        } else {
            expression = new ImmutableNamedSingleParam(type, name);
        }
        return expression;
    }


    /**
     * @throws CriteriaException throw when infer isn't codec {@link TableField}.
     * @see SQLs#encodingParam(TypeInfer, Object)
     */
    static SingleParamExpression encodingSingle(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("param");
        }
        return new ImmutableAnonymousSingleParam((TableField) infer, value);
    }


    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedParam(TypeInfer, String)
     */
    static SingleParamExpression encodingNamed(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedParam");
        }
        return new ImmutableNamedNonNullSingleParam((TableField) infer, name);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link FieldMeta}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedNullableParam(TypeInfer, String)
     */
    static SingleParamExpression encodingNamedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedNullableParam");
        }
        return new ImmutableNamedSingleParam((TableField) infer, name);
    }

    static TypeMeta inferDelayType(final TypeInfer.DelayTypeInfer infer, final String encodingMethod) {
        if (infer.isDelay()) {
            throw CriteriaUtils.delayTypeInfer(infer);
        }
        TypeMeta type;
        type = infer.typeMeta();

        if ((type instanceof TableField && ((TableField) type).codec())) {
            throw SingleParamExpression.typeInferReturnCodecField(encodingMethod);
        }
        if (type instanceof QualifiedField) {
            type = ((QualifiedField<?>) type).fieldMeta();
        } else {
            assert type instanceof FieldMeta || type instanceof MappingType;
        }
        return type;
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


    @Override
    public final void appendSql(_SqlContext context) {
        context.appendParam(this);
    }


    private static abstract class AnonymousSingleParam extends SingleParamExpression
            implements SingleParam, SingleAnonymousValue {

        final Object value;


        private AnonymousSingleParam(@Nullable Object value) {
            this.value = value;
        }

        @Override
        public final Object value() {
            return this.value;
        }

        @Override
        public final String toString() {
            return " ?";
        }


    }//AnonymousSingleParam

    private static final class ImmutableAnonymousSingleParam extends AnonymousSingleParam {

        private final TypeMeta type;

        /**
         * @see #single(TypeInfer, Object)
         */
        private ImmutableAnonymousSingleParam(TypeMeta type, @Nullable Object value) {
            super(value);
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
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
            } else if (obj instanceof ImmutableAnonymousSingleParam) {
                final ImmutableAnonymousSingleParam o = (ImmutableAnonymousSingleParam) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableAnonymousSingleParam


    private static final class DelayAnonymousSingleParam extends AnonymousSingleParam
            implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;


        private TypeMeta type;

        /**
         * @see #single(TypeInfer, Object)
         */
        private DelayAnonymousSingleParam(DelayTypeInfer infer, @Nullable Object value) {
            super(value);
            this.infer = infer;
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                type = SingleParamExpression.inferDelayType(this.infer, "encodingParam");
                this.type = type;
            }
            return type;
        }


    }//DelayAnonymousSingleParam


    private static abstract class NamedSingleParam extends SingleParamExpression implements NamedParam.NamedSingle {

        final String name;

        private NamedSingleParam(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final String toString() {
            return " ?:" + this.name;
        }


    }//NamedSingleParam


    private static class ImmutableNamedSingleParam extends NamedSingleParam {

        private final TypeMeta type;

        /**
         * @see #namedNullable(TypeInfer, String)
         * @see #encodingNamedNullable(TypeInfer, String)
         */
        private ImmutableNamedSingleParam(TypeMeta type, String name) {
            super(name);
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
        public final int hashCode() {
            return Objects.hash(this.type, this.name);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ImmutableNamedSingleParam) {
                final ImmutableNamedSingleParam o = (ImmutableNamedSingleParam) obj;
                match = o instanceof ImmutableNamedNonNullSingleParam == this instanceof ImmutableNamedNonNullSingleParam
                        && o.type.equals(this.type)
                        && o.name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableNamedSingleParam

    private static final class ImmutableNamedNonNullSingleParam extends ImmutableNamedSingleParam
            implements SqlValueParam.NonNullValue {

        /**
         * @see #named(TypeInfer, String)
         * @see #encodingNamed(TypeInfer, String)
         */
        private ImmutableNamedNonNullSingleParam(TypeMeta type, String name) {
            super(type, name);
        }


    }//ImmutableNonNullNamedSingleParam

    private static class DelayNamedSingleParam extends NamedSingleParam implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #named(TypeInfer, String)
         * @see #encodingNamed(TypeInfer, String)
         */
        private DelayNamedSingleParam(DelayTypeInfer infer, String name) {
            super(name);
            this.infer = infer;
        }

        @Override
        public final boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public final TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                if (this instanceof DelayNamedNonNullSingleParam) {
                    type = SingleParamExpression.inferDelayType(this.infer, "encodingNamedParam");
                } else {
                    type = SingleParamExpression.inferDelayType(this.infer, "encodingNamedNullableParam");
                }
                this.type = type;
            }
            return type;
        }

    }//DelayNamedSingleParam


    private static final class DelayNamedNonNullSingleParam extends DelayNamedSingleParam
            implements SqlValueParam.NonNullValue {

        /**
         * @see #named(TypeInfer, String)
         * @see #encodingNamed(TypeInfer, String)
         */
        private DelayNamedNonNullSingleParam(DelayTypeInfer infer, String name) {
            super(infer, name);
        }

    }//DelayNamedNonNullSingleParam


}
