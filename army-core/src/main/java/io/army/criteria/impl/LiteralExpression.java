package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
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
 * @see ParamExpression
 * @see LiteralRowExpression
 * @see ParamRowExpression
 * @since 1.0
 */
abstract class LiteralExpression extends OperationExpression.OperationSimpleExpression
        implements SqlValueParam.SingleValue {

    /**
     * @see SQLs#literalValue(Object)
     */
    static LiteralExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final MappingType type;
        type = _MappingFactory.getDefaultIfMatch(value.getClass());
        if (type == null) {
            throw CriteriaUtils.clearStackAndNonDefaultType(value);
        }
        return new AnonymousLiteral(type, value);
    }


    /**
     * @throws CriteriaException throw when infer return codec {@link TableField}.
     * @see SQLs#literal(TypeInfer, Object)
     */
    static LiteralExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingLiteral");
        }
        return new AnonymousLiteral(type, value);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedLiteral(TypeInfer, String)
     */
    static LiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingNamedLiteral");
        }
        return new NamedNonNullLiteral(name, type);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer return codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#namedNullableLiteral(TypeInfer, String)
     */
    static LiteralExpression namedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingNamedNullableLiteral");
        }
        return new NamedLiteral(name, type);
    }


    /**
     * @throws CriteriaException throw when infer isn't codec {@link TableField}.
     * @see SQLs#encodingLiteral(TypeInfer, Object)
     */
    static LiteralExpression encodingSingle(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("literal");
        }
        return new AnonymousLiteral((TableField) infer, value);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedLiteral(TypeInfer, String)
     */
    static LiteralExpression encodingNamed(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("namedLiteral");
        }
        return new NamedNonNullLiteral(name, (TableField) infer);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>infer isn't codec {@link TableField}.</li>
     *                           <li>name have no text</li>
     *                           </ul>
     * @see SQLs#encodingNamedNullableLiteral(TypeInfer, String)
     */
    static LiteralExpression encodingNamedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("namedNullableLiteral");
        }
        return new NamedLiteral(name, (TableField) infer);
    }

    private static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for single-literal.");
    }

    /**
     * private constructor
     */
    private LiteralExpression() {
    }

    private static class AnonymousLiteral extends LiteralExpression
            implements SingleAnonymousValue {

        private final Object value;

        private final TypeMeta type;

        /**
         * @see #single(TypeInfer, Object)
         * @see #encodingSingle(TypeInfer, Object)
         */
        private AnonymousLiteral(TypeMeta type, @Nullable Object value) {
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.value = value;
        }

        @Override
        public final Object value() {
            return this.value;
        }


        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public final void appendSql(_SqlContext context) {
            context.appendLiteral(this.type, this.value);
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


    private static class NamedLiteral extends LiteralExpression implements io.army.criteria.NamedLiteral {

        private final String name;


        private final TypeMeta type;

        private NamedLiteral(String name, TypeMeta type) {
            this.name = name;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
        }


        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.appendLiteral(this);
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

        private NamedNonNullLiteral(String name, TypeMeta type) {
            super(name, type);
        }


    }//NamedNonNullLiteral


}
