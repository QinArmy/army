package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

import javax.annotation.Nullable;

import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.util._Collections;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * This class representing multi-value literal expression.
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @see ArmyParamExpression
 * @see ArmyLiteralExpression
 * @see ArmyRowParamExpression
 * @since 1.0
 */

abstract class ArmyRowLiteralExpression extends OperationRowExpression implements
        RowLiteralExpression, ArmySimpleSQLExpression {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    static ArmyRowLiteralExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingMultiLiteral");
        }
        return new AnonymousMultiLiteral(type, values);
    }


    @Deprecated
    static ArmyRowLiteralExpression unsafeMulti(final @Nullable TypeInfer infer, final @Nullable List<?> values) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingMultiLiteral");
        }
        return new AnonymousMultiLiteral(type, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedRowLiteral(TypeInfer, String, int)
     */

    static ArmyRowLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name, final int size) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedMultiLiteral");
        }
        return new NamedMultiLiteral(name, type, size);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingRowLiteral(TypeInfer, Collection)
     */
    static ArmyRowLiteralExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("multiLiteral");
        }
        return new AnonymousMultiLiteral((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedRowLiteral(TypeInfer, String, int)
     */
    static ArmyRowLiteralExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedMultiLiteral");
        }
        return new NamedMultiLiteral(name, (TableField) infer, size);
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
    private ArmyRowLiteralExpression() {
    }


    static final class AnonymousMultiLiteral extends ArmyRowLiteralExpression {

        private final TypeMeta type;

        private final List<?> valueList;

        /**
         * @see #multi(TypeInfer, Collection)
         */
        private AnonymousMultiLiteral(TypeMeta type, Collection<?> values) {
            assert values.size() > 0;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.valueList = _Collections.asUnmodifiableList(values);
        }


        @Override
        public int columnSize() {
            return this.valueList.size();
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }


        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            final List<?> valueList = this.valueList;
            final int valueSize = valueList.size();
            assert valueSize > 0;

            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            final TypeMeta type = this.type;
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendLiteral(type, valueList.get(i));
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final boolean encoding;
            final TypeMeta type = this.type;
            encoding = type instanceof TableField && ((TableField) type).codec();

            final List<?> valueList = this.valueList;
            final int valueSize = valueList.size();
            assert valueSize > 0;

            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

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

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
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
            } else if (obj instanceof AnonymousMultiLiteral) {
                final AnonymousMultiLiteral o = (AnonymousMultiLiteral) obj;
                match = o.type.equals(this.type)
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }


    }//AnonymousMultiLiteral


    private static final class NamedMultiLiteral extends ArmyRowLiteralExpression implements NamedLiteral,
            SqlValueParam.NamedMultiValue {

        private final String name;

        private final TypeMeta type;

        private final int valueSize;

        /**
         * @see #named(TypeInfer, String, int)
         * @see #encodingNamed(TypeInfer, String, int)
         */
        private NamedMultiLiteral(String name, TypeMeta type, int valueSize) {
            assert valueSize > 0;
            this.name = name;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.valueSize = valueSize;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public int columnSize() {
            return this.valueSize;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            context.appendLiteral(this);
        }


        @Override
        public String toString() {
            final int valueSize = this.valueSize;
            final String name = this.name;
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

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
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
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


    }//NamedMultiLiteral


}
