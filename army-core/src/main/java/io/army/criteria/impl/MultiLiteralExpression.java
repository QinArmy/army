package io.army.criteria.impl;

import io.army.criteria.NamedLiteral;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
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
 * @see SingleLiteralExpression
 * @since 1.0
 */

abstract class MultiLiteralExpression extends OperationExpression.MultiValueExpression
        implements SqlValueParam.MultiValue {

    /**
     * @see SQLs#multiLiteral(TypeInfer, Collection)
     */
    static MultiLiteralExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw ContextStack.clearStackAndCriteriaError("values must non-empty");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NonNamedMultiLiteral(type, values);
    }


    static MultiLiteralExpression safeMulti(final TypeMeta type, final List<?> values) {
        assert values.size() > 0;
        return new NonNamedMultiLiteral(values, type);
    }

    /**
     * @see SQLs#namedMultiLiteral(TypeInfer, String, int)
     */

    static MultiLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name, int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("named multi-literal must have text.");
        } else if (size < 1) {
            throw ContextStack.clearStackAndCriteriaError("size must great than zero.");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NamedMultiLiteral(type, name, size);
    }


    final TypeMeta type;

    private MultiLiteralExpression(TypeMeta type) {
        this.type = type;
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
