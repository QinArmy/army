package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.Objects;


/**
 * <p>
 * This class representing single-literal expression.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see SingleParamExpression
 * @see MultiLiteralExpression
 * @see MultiParamExpression
 * @since 1.0
 */
abstract class SingleLiteralExpression extends OperationExpression.OperationSimpleExpression
        implements SqlValueParam.SingleValue {

    /**
     * @see SQLs#literalValue(Object)
     */
    static SingleLiteralExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        try {
            return new ImmutableAnonymousSingleLiteral(_MappingFactory.getDefault(value.getClass()), value);
        } catch (MetaException e) {
            throw ContextStack.clearStackAndCriteriaError(e.getMessage());
        }
    }


    /**
     * @throws CriteriaException throw when infer return codec {@link TableField}.
     * @see SQLs#literal(TypeInfer, Object)
     */
    static SingleLiteralExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        final AnonymousSingleLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayAnonymousSingleLiteral((DelayTypeInfer) infer, value);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingLiteral");
        } else {
            expression = new ImmutableAnonymousSingleLiteral(type, value);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        final NamedSingleLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedNonNullSingleLiteral((DelayTypeInfer) infer, name);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedLiteral");
        } else {
            expression = new ImmutableNamedNonNullSingleLiteral(type, name);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedNullableLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression namedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        final NamedSingleLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedSingleLiteral((DelayTypeInfer) infer, name);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedNullableLiteral");
        } else {
            expression = new ImmutableNamedSingleLiteral(type, name);
        }
        return expression;
    }


    /**
     * @throws CriteriaException throw when infer isn't codec {@link TableField}.
     * @see SQLs#encodingLiteral(TypeInfer, Object)
     */
    static SingleLiteralExpression encodingSingle(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("literal");
        }
        return new ImmutableAnonymousSingleLiteral((TableField) infer, value);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression encodingNamed(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedLiteral");
        }
        return new ImmutableNamedNonNullSingleLiteral((TableField) infer, name);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedNullableLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression encodingNamedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedNullableLiteral");
        }
        return new ImmutableNamedSingleLiteral((TableField) infer, name);
    }

    private static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for single-literal.");
    }

    /**
     * private constructor
     */
    private SingleLiteralExpression() {
    }

    private static abstract class AnonymousSingleLiteral extends SingleLiteralExpression
            implements SingleAnonymousValue {

        final Object value;

        private AnonymousSingleLiteral(@Nullable Object value) {
            this.value = value;
        }

        @Override
        public final Object value() {
            return this.value;
        }


    }//AnonymousSingleLiteral


    private static final class ImmutableAnonymousSingleLiteral extends AnonymousSingleLiteral {

        private final TypeMeta type;

        /**
         * @see #single(TypeInfer, Object)
         * @see #encodingSingle(TypeInfer, Object)
         */
        private ImmutableAnonymousSingleLiteral(TypeMeta type, @Nullable Object value) {
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
        public void appendSql(_SqlContext context) {
            context.appendLiteral(this.type, this.value);
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
            } else if (obj instanceof ImmutableAnonymousSingleLiteral) {
                final ImmutableAnonymousSingleLiteral o = (ImmutableAnonymousSingleLiteral) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
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


    }//ImmutableAnonymousSingleLiteral


    private static final class DelayAnonymousSingleLiteral extends AnonymousSingleLiteral
            implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;


        private TypeMeta type;

        /**
         * @see #single(TypeInfer, Object)
         */
        private DelayAnonymousSingleLiteral(TypeInfer.DelayTypeInfer infer, @Nullable Object value) {
            super(value);
            this.infer = infer;
            ContextStack.peek().addEndEventListener(this::typeMeta);
        }


        @Override
        public void appendSql(_SqlContext context) {
            context.appendLiteral(this.typeMeta(), this.value);
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                type = SingleParamExpression.inferDelayType(this.infer, "encodingLiteral");
                this.type = type;
            }
            return type;
        }


        @Override
        public String toString() {
            final TypeMeta type = this.type;
            final String s;
            if (this.value == null) {
                s = _Constant.SPACE_NULL;
            } else if (type == null || (type instanceof TableField && ((TableField) type).codec())) {
                s = " {LITERAL}";
            } else {
                s = " " + this.value;
            }
            return s;
        }


    }//DelayAnonymousNonNullSingleLiteral


    private static abstract class NamedSingleLiteral extends SingleLiteralExpression implements NamedLiteral {

        final String name;

        private NamedSingleLiteral(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.appendLiteral(this);
        }


        @Override
        public final String toString() {
            return " ?:" + this.name;
        }


    }//NamedSingleLiteral


    private static class ImmutableNamedSingleLiteral extends NamedSingleLiteral {

        private final TypeMeta type;

        private ImmutableNamedSingleLiteral(TypeMeta type, String name) {
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
            } else if (obj instanceof ImmutableNamedSingleLiteral) {
                final ImmutableNamedSingleLiteral o = (ImmutableNamedSingleLiteral) obj;
                match = o instanceof ImmutableNamedNonNullSingleLiteral == this instanceof ImmutableNamedNonNullSingleLiteral
                        && o.type.equals(this.type)
                        && o.name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableNamedSingleLiteral

    private static final class ImmutableNamedNonNullSingleLiteral extends ImmutableNamedSingleLiteral
            implements SqlValueParam.NonNullValue {

        private ImmutableNamedNonNullSingleLiteral(TypeMeta type, String name) {
            super(type, name);
        }


    }//ImmutableNamedNonNullSingleLiteral


    private static class DelayNamedSingleLiteral extends NamedSingleLiteral implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #namedNullable(TypeInfer, String)
         */
        private DelayNamedSingleLiteral(DelayTypeInfer infer, String name) {
            super(name);
            this.infer = infer;
            ContextStack.peek().addEndEventListener(this::typeMeta);
        }

        @Override
        public final boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public final TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                if (this instanceof DelayNamedNonNullSingleLiteral) {
                    type = SingleParamExpression.inferDelayType(this.infer, "encodingNamedLiteral");
                } else {
                    type = SingleParamExpression.inferDelayType(this.infer, "encodingNamedNullableLiteral");
                }
                this.type = type;
            }
            return type;
        }


    }//DelayNamedSingleLiteral


    private static final class DelayNamedNonNullSingleLiteral extends DelayNamedSingleLiteral
            implements SqlValueParam.NonNullValue {

        /**
         * @see #named(TypeInfer, String)
         */
        private DelayNamedNonNullSingleLiteral(DelayTypeInfer infer, String name) {
            super(infer, name);
        }

    }//DelayNamedNonNullSingleLiteral


}
