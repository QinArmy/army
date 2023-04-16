package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.*;

/**
 * <p>
 * This class representing multi-value literal expression.
 * </p>
 *
 * @see SingleParamExpression
 * @see SingleLiteralExpression
 * @see MultiParamExpression
 * @since 1.0
 */

abstract class MultiLiteralExpression extends NonOperationExpression.MultiValueExpression {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#multiLiteral(TypeInfer, Collection)
     */
    static MultiLiteralExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingMultiLiteral");
        }
        return new NonNamedMultiLiteral(type, values);
    }


    static MultiLiteralExpression safeMulti(final TypeMeta type, final List<?> values) {
        assert values.size() > 0;
        return new NonNamedMultiLiteral(values, type);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedMultiLiteral(TypeInfer, String, int)
     */

    static MultiLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name, int size) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedMultiLiteral");
        }
        return new NamedMultiLiteral(type, name, size);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingMultiLiteral(TypeInfer, Collection)
     */
    static MultiLiteralExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("multiLiteral");
        }
        return new NonNamedMultiLiteral((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedMultiLiteral(TypeInfer, String, int)
     */
    static MultiLiteralExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw SingleParamExpression.typeInferIsNotCodecField("namedMultiLiteral");
        }
        return new NamedMultiLiteral((TableField) infer, name, size);
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


    private MultiLiteralExpression(TypeMeta type) {
        super(type);
    }

    @Override
    public final TypeMeta typeMeta() {
        return this.type;
    }

    static final class NonNamedMultiLiteral extends MultiLiteralExpression
            implements SqlValueParam.MultiValue {

        private final List<?> valueList;

        private NonNamedMultiLiteral(TypeMeta type, Collection<?> values) {
            super(type);
            this.valueList = Collections.unmodifiableList(new ArrayList<>(values));
        }

        /**
         * for {@link #safeMulti(TypeMeta, List)}
         *
         * @see #safeMulti(TypeMeta, List)
         */
        private NonNamedMultiLiteral(List<?> values, TypeMeta paramMeta) {
            super(paramMeta);
            this.valueList = Collections.unmodifiableList(values);
            assert this.valueList.size() > 0;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final List<?> valueList = this.valueList;
            final int valueSize = valueList.size();
            assert valueSize > 0;

            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder();
            final TypeMeta type = this.type;
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                } else {
                    sqlBuilder.append(_Constant.SPACE);
                }
                context.appendLiteral(type, valueList.get(i));
            }
        }


        @Override
        public int valueSize() {
            return this.valueList.size();
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
            } else if (obj instanceof NonNamedMultiLiteral) {
                final NonNamedMultiLiteral o = (NonNamedMultiLiteral) obj;
                match = o.type.equals(this.type)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
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
                builder.append(valueList.get(i));
            }
            return builder.toString();
        }


    }//NonNamedMultiLiteral


    static final class NamedMultiLiteral extends MultiLiteralExpression implements NamedLiteral,
            SqlValueParam.NamedMultiValue {

        private final String name;

        private final int valueSize;

        private NamedMultiLiteral(TypeMeta type, String name, int valueSize) {
            super(type);
            assert valueSize > 0;
            this.name = name;
            this.valueSize = valueSize;
        }

        @Override
        public int valueSize() {
            return this.valueSize;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendLiteral(this);
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
            } else if (obj instanceof NamedMultiLiteral) {
                final NamedMultiLiteral o = (NamedMultiLiteral) obj;
                match = o.type.equals(this.type)
                        && o.name.equals(this.name)
                        && o.valueSize == this.valueSize;
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
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


}
