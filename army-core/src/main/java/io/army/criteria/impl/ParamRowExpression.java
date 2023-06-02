package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.MultiParam;
import io.army.util._StringUtils;

import java.util.*;


/**
 * <p>
 * This class representing multi-value parameter expression.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see ParamExpression
 * @see LiteralExpression
 * @see LiteralRowExpression
 * @since 1.0
 */
abstract class ParamRowExpression extends OperationRowExpression
        implements SqlValueParam.MultiValue, SQLParam {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#rowParam(TypeInfer, Collection)
     */
    static ParamRowExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final AnonymousMultiParam expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayAnonymousMultiParam((DelayTypeInfer) infer, values);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingMultiParam");
        } else {
            expression = new ImmutableAnonymousMultiParam(type, values);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedRowParam(TypeInfer, String, int)
     */
    static ParamRowExpression named(final @Nullable TypeInfer infer, final @Nullable String name, final int size) {
        final NamedMultiParam expression;
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (infer instanceof TypeInfer.DelayTypeInfer && ((DelayTypeInfer) infer).isDelay()) {
            expression = new DelayNamedMultiParam((DelayTypeInfer) infer, name, size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ParamExpression.typeInferReturnCodecField("encodingNamedMultiParam");
        } else {
            expression = new ImmutableNamedMultiParam(type, name, size);
        }
        return expression;
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingRowParam(TypeInfer, Collection)
     */
    static ParamRowExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("multiParam");
        }
        return new ImmutableAnonymousMultiParam((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedRowParam(TypeInfer, String, int)
     */
    static ParamRowExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ParamExpression.typeInferIsNotCodecField("namedMultiParam");
        }
        return new ImmutableNamedMultiParam((TableField) infer, name, size);
    }

    private static CriteriaException valuesIsEmpty() {
        return ContextStack.clearStackAndCriteriaError("values must non-empty for multi-value parameter.");
    }

    private static CriteriaException nameHaveNoText() {
        return ContextStack.clearStackAndCriteriaError("name must have text for multi-value named parameter.");
    }

    private static CriteriaException sizeLessThanOne(final int size) {
        final String m = String.format("size[%s] must greater than 0 for multi-value named parameter.", size);
        return ContextStack.clearStackAndCriteriaError(m);
    }

    /**
     * private constructor
     */
    private ParamRowExpression() {
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.appendParam(this);
    }

    private static abstract class AnonymousMultiParam extends ParamRowExpression implements MultiParam {

        final List<?> valueList;

        public AnonymousMultiParam(Collection<?> values) {
            this.valueList = Collections.unmodifiableList(new ArrayList<>(values));
        }


        @Override
        public final int columnSize() {
            return this.valueList.size();
        }

        @Override
        public final List<?> valueList() {
            return this.valueList;
        }

        @Override
        public final String toString() {
            final int valueSize;
            valueSize = this.valueList.size();
            final StringBuilder builder = new StringBuilder(valueSize << 2);
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(" ?");
            }
            return builder.toString();
        }


    }//AnonymousMultiParam


    private static final class ImmutableAnonymousMultiParam extends AnonymousMultiParam {

        private final TypeMeta type;

        private ImmutableAnonymousMultiParam(TypeMeta type, Collection<?> values) {
            super(values);
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
            } else if (obj instanceof ImmutableAnonymousMultiParam) {
                final ImmutableAnonymousMultiParam o = (ImmutableAnonymousMultiParam) obj;
                match = o.type.equals(this.type)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableAnonymousMultiParam

    private static final class DelayAnonymousMultiParam extends AnonymousMultiParam
            implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #multi(TypeInfer, Collection)
         */
        private DelayAnonymousMultiParam(DelayTypeInfer infer, Collection<?> values) {
            super(values);
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
                type = ParamExpression.inferDelayType(this.infer, "encodingMultiParam");
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
            } else if (obj instanceof DelayAnonymousMultiParam) {
                final DelayAnonymousMultiParam o = (DelayAnonymousMultiParam) obj;
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


    }//DelayAnonymousMultiParam


    private static abstract class NamedMultiParam extends ParamRowExpression implements NamedParam.NamedMulti {

        final String name;

        final int valueSize;

        private NamedMultiParam(String name, int valueSize) {
            assert valueSize > 0;
            this.name = name;
            this.valueSize = valueSize;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final int columnSize() {
            return this.valueSize;
        }

        @Override
        public final String toString() {
            final int valueSize = this.valueSize;
            final StringBuilder builder = new StringBuilder((5 + this.name.length()) * valueSize);
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(" ?:")
                        .append(this.name);
            }
            return builder.toString();
        }


    }//NamedMultiParam


    private static final class ImmutableNamedMultiParam extends NamedMultiParam {

        private final TypeMeta type;

        /**
         * @see #named(TypeInfer, String, int)
         * @see #encodingNamed(TypeInfer, String, int)
         */
        private ImmutableNamedMultiParam(TypeMeta type, String name, int valueSize) {
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
            } else if (obj instanceof ImmutableNamedMultiParam) {
                final ImmutableNamedMultiParam o = (ImmutableNamedMultiParam) obj;
                match = o.type.equals(this.type)
                        && o.name.equals(this.name)
                        && o.valueSize == this.valueSize;
            } else {
                match = false;
            }
            return match;
        }


    }//ImmutableNamedMultiParam

    private static final class DelayNamedMultiParam extends NamedMultiParam implements TypeInfer.DelayTypeInfer {

        private final TypeInfer.DelayTypeInfer infer;

        private TypeMeta type;

        /**
         * @see #named(TypeInfer, String, int)
         */
        private DelayNamedMultiParam(DelayTypeInfer infer, String name, int valueSize) {
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
                type = ParamExpression.inferDelayType(this.infer, "encodingNamedMultiParam");
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
            } else if (obj instanceof DelayNamedMultiParam) {
                final DelayNamedMultiParam o = (DelayNamedMultiParam) obj;
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


    }//DelayNamedMultiParam


}
