package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.stmt.MultiParam;
import io.army.util._StringUtils;

import java.util.*;


/**
 * <p>
 * This class representing multi-value parameter expression.
 * </p>
 *
 * @see SingleParamExpression
 * @see SingleLiteralExpression
 * @see MultiLiteralExpression
 * @since 1.0
 */
abstract class MultiParamExpression extends OperationExpression.MultiValueExpression implements SQLParam {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#multiParam(TypeInfer, Collection)
     */
    static MultiParamExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingMultiParam");
        }
        return new NonNameMultiParam(type, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedMultiParam(TypeInfer, String, int)
     */
    static MultiParamExpression named(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedMultiParam");
        }
        return new NamedMultiParamExpression(type, name, size);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingMultiParam(TypeInfer, Collection)
     */
    static MultiParamExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("multiParam");
        }
        return new NonNameMultiParam((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedMultiParam(TypeInfer, String, int)
     */
    static MultiParamExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedMultiParam");
        }
        return new NamedMultiParamExpression((TableField) infer, name, size);
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


    final TypeMeta type;


    private MultiParamExpression(TypeMeta type) {
        if (type instanceof QualifiedField) {
            this.type = ((QualifiedField<?>) type).fieldMeta();
        } else {
            this.type = type;
        }
    }


    @Override
    public final TypeMeta typeMeta() {
        return this.type;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.appendParam(this);
    }

    private static final class NonNameMultiParam extends MultiParamExpression
            implements MultiParam {

        private final List<?> valueList;

        public NonNameMultiParam(TypeMeta type, Collection<?> values) {
            super(type);
            this.valueList = Collections.unmodifiableList(new ArrayList<>(values));
        }


        @Override
        public int valueSize() {
            return this.valueList.size();
        }

        @Override
        public List<?> valueList() {
            return this.valueList;
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
            } else if (obj instanceof NonNameMultiParam) {
                final NonNameMultiParam o = (NonNameMultiParam) obj;
                match = o.type.equals(this.type)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
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


    }//NonNameMultiParamExpression

    private static final class NamedMultiParamExpression extends MultiParamExpression
            implements NamedParam.NamedMulti {

        private final String name;

        private final int size;

        private NamedMultiParamExpression(TypeMeta type, String name, int size) {
            super(type);
            this.name = name;
            this.size = size;
        }


        @Override
        public int valueSize() {
            return this.size;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.name, this.size);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedMultiParamExpression) {
                final NamedMultiParamExpression o = (NamedMultiParamExpression) obj;
                match = o.type.equals(this.type)
                        && o.name.equals(this.name)
                        && o.size == this.size;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final int valueSize = this.size;
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


    }//NamedMultiParamExpression


}
