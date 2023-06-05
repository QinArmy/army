package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.*;

/**
 * <p>
 * This class representing multi-value literal expression.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see ParamExpression
 * @see LiteralExpression
 * @see ParamRowExpression
 * @since 1.0
 */

abstract class LiteralRowExpression extends OperationRowExpression implements
        SqlValueParam.MultiValue {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    static LiteralRowExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final AnonymousMultiLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) infer).isDelay()) {
            expression = new DelayAnonymousMultiLiteral((TypeInfer.DelayTypeInfer) infer, values);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingMultiLiteral");
        } else {
            expression = new ImmutableAnonymousMultiLiteral(type, values);
        }
        return expression;
    }


    @Deprecated
    static LiteralRowExpression unsafeMulti(final @Nullable TypeInfer infer, final @Nullable List<?> values) {
        final AnonymousMultiLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) infer).isDelay()) {
            expression = new DelayAnonymousMultiLiteral(values, (TypeInfer.DelayTypeInfer) infer);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingMultiLiteral");
        } else {
            expression = new ImmutableAnonymousMultiLiteral(values, type);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedRowLiteral(TypeInfer, String, int)
     */

    static LiteralRowExpression named(final @Nullable TypeInfer infer, final @Nullable String name, final int size) {
        final NamedMultiLiteral expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((TypeInfer.DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedMultiLiteral((TypeInfer.DelayTypeInfer) infer, name, size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingNamedMultiLiteral");
        } else {
            expression = new ImmutableNamedMultiLiteral(type, name, size);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingRowLiteral(TypeInfer, Collection)
     */
    static LiteralRowExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("multiLiteral");
        }
        return new ImmutableAnonymousMultiLiteral((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedRowLiteral(TypeInfer, String, int)
     */
    static LiteralRowExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("namedMultiLiteral");
        }
        return new ImmutableNamedMultiLiteral((TableField) infer, name, size);
    }

    private static CriteriaException valuesIsEmpty() {
        return ContextStack.clearStackAndCriteriaError("values must non-empty for multi-value literal.");
    }

    private static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for multi-value named literal.");
    }

    private static CriteriaException sizeLessThanOne(final int size) {
        final String m = String.format("size[%s] must greater than 0 for multi-value named literal.", size);
        return ContextStack.clearStackAndCriteriaError(m);
    }


    /**
     * private constructor
     */
    private LiteralRowExpression() {
    }


    static abstract class AnonymousMultiLiteral extends LiteralRowExpression {

        final List<?> valueList;

        private AnonymousMultiLiteral(List<?> unmodifiedList) {
            assert unmodifiedList.size() > 0;
            this.valueList = unmodifiedList;
        }

        @Override
        public final int columnSize() {
            return this.valueList.size();
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            final List<?> valueList = this.valueList;
            final int valueSize = valueList.size();
            assert valueSize > 0;

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final TypeMeta type;
            type = this.typeMeta();
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendLiteral(type, valueList.get(i));
            }
        }


        @Override
        public final String toString() {
            final boolean encoding;
            if (this instanceof ImmutableAnonymousMultiLiteral) {
                final TypeMeta type = ((ImmutableAnonymousMultiLiteral) this).type;
                encoding = type instanceof TableField && ((TableField) type).codec();
            } else if (this instanceof DelayAnonymousMultiLiteral) {
                final TypeMeta type = ((DelayAnonymousMultiLiteral) this).type;
                encoding = type == null || (type instanceof TableField && ((TableField) type).codec());
            } else {
                encoding = true;
            }
            final List<?> valueList = this.valueList;
            final int valueSize = valueList.size();
            assert valueSize > 0;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    builder.append(_Constant.SPACE);
                }
                if (encoding) {
                    builder.append("{LITERAL}");
                } else {
                    builder.append(valueList.get(i));
                }

            }
            return builder.toString();
        }


    }//AnonymousMultiLiteral

    private static final class ImmutableAnonymousMultiLiteral extends AnonymousMultiLiteral {

        private final TypeMeta type;

        /**
         * @see #multi(TypeInfer, Collection)
         */
        private ImmutableAnonymousMultiLiteral(TypeMeta type, Collection<?> values) {
            super(Collections.unmodifiableList(new ArrayList<>(values)));
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
        }

        /**
         * @see #unsafeMulti(TypeInfer, List)
         */
        private ImmutableAnonymousMultiLiteral(List<?> unsafeList, TypeMeta type) {
            super(Collections.unmodifiableList(unsafeList));
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
            return Objects.hash(this.type, this.valueList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ImmutableAnonymousMultiLiteral) {
                final ImmutableAnonymousMultiLiteral o = (ImmutableAnonymousMultiLiteral) obj;
                match = o.type.equals(this.type)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableAnonymousMultiLiteral

    private static final class DelayAnonymousMultiLiteral extends AnonymousMultiLiteral
            implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #multi(TypeInfer, Collection)
         */
        private DelayAnonymousMultiLiteral(TypeInfer.DelayTypeInfer infer, Collection<?> values) {
            super(Collections.unmodifiableList(new ArrayList<>(values)));
            this.infer = infer;
            ContextStack.peek().addEndEventListener(this::typeMeta);
        }

        /**
         * @see #unsafeMulti(TypeInfer, List)
         */
        private DelayAnonymousMultiLiteral(List<?> unsafeList, TypeInfer.DelayTypeInfer infer) {
            super(Collections.unmodifiableList(unsafeList));
            this.infer = infer;
            ContextStack.peek().addEndEventListener(this::onContextEnd);
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                type = ParamExpression.inferDelayType(this.infer, "encodingMultiLiteral");
                this.type = type;
            }
            return type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.infer, this.valueList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof DelayAnonymousMultiLiteral) {
                final DelayAnonymousMultiLiteral o = (DelayAnonymousMultiLiteral) obj;
                match = o.infer.equals(this.infer)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }

        private void onContextEnd() {
            if (!this.infer.isDelay()) {
                this.typeMeta();
            } else if (ContextStack.isEmpty()) {
                throw CriteriaUtils.delayTypeInfer(this.infer);
            } else {
                //here, possibly recursive reference in WITH RECURSIVE clause
                ContextStack.peek().addEndEventListener(this::onContextEnd);
            }
        }


    }//DelayAnonymousMultiLiteral


    private static abstract class NamedMultiLiteral extends LiteralRowExpression implements NamedLiteral,
            SqlValueParam.NamedMultiValue {

        final String name;

        final int valueSize;

        private NamedMultiLiteral(String name, int valueSize) {
            assert valueSize > 0;
            this.name = name;
            this.valueSize = valueSize;
        }

        @Override
        public final int columnSize() {
            return this.valueSize;
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
            final int valueSize = this.valueSize;
            final String name = this.name;
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA_SPACE);
                } else {
                    builder.append(_Constant.SPACE);
                }
                builder.append(" ?:")
                        .append(name)
                        .append("{[")
                        .append(i)
                        .append("]}");
            }
            return builder.toString();
        }


    }//NamedMultiLiteral

    private static final class ImmutableNamedMultiLiteral extends NamedMultiLiteral {

        private final TypeMeta type;

        /**
         * @see #named(TypeInfer, String, int)
         * @see #encodingNamed(TypeInfer, String, int)
         */
        private ImmutableNamedMultiLiteral(TypeMeta type, String name, int valueSize) {
            super(name, valueSize);
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
            return Objects.hash(this.type, this.name, this.valueSize);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof ImmutableNamedMultiLiteral) {
                final ImmutableNamedMultiLiteral o = (ImmutableNamedMultiLiteral) obj;
                match = o.type.equals(this.type)
                        && o.name.equals(this.name)
                        && o.valueSize == this.valueSize;
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableNamedMultiLiteral


    private static final class DelayNamedMultiLiteral extends NamedMultiLiteral implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #named(TypeInfer, String, int)
         */
        private DelayNamedMultiLiteral(DelayTypeInfer infer, String name, int valueSize) {
            super(name, valueSize);
            this.infer = infer;
            ContextStack.peek().addEndEventListener(this::onContextEnd);
        }

        @Override
        public boolean isDelay() {
            return this.type == null && this.infer.isDelay();
        }

        @Override
        public TypeMeta typeMeta() {
            TypeMeta type = this.type;
            if (type == null) {
                type = ParamExpression.inferDelayType(this.infer, "encodingNamedMultiLiteral");
                this.type = type;
            }
            return type;
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.infer, this.name, this.valueSize);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof DelayNamedMultiLiteral) {
                final DelayNamedMultiLiteral o = (DelayNamedMultiLiteral) obj;
                match = o.infer.equals(this.infer)
                        && o.name.equals(this.name)
                        && o.valueSize == this.valueSize;
            } else {
                match = false;
            }
            return match;
        }

        private void onContextEnd() {
            if (!this.infer.isDelay()) {
                this.typeMeta();
            } else if (ContextStack.isEmpty()) {
                throw CriteriaUtils.delayTypeInfer(this.infer);
            } else {
                //here, possibly recursive reference in WITH RECURSIVE clause
                ContextStack.peek().addEndEventListener(this::onContextEnd);
            }
        }


    }//DelayNamedMultiLiteral


}