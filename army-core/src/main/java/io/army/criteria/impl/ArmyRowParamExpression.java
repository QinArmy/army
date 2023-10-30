package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;

import javax.annotation.Nullable;

import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;
import io.army.stmt.MultiParam;
import io.army.util._Collections;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * This class representing multi-value parameter expression.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @see ArmyParamExpression
 * @see ArmyLiteralExpression
 * @see ArmyRowLiteralExpression
 * @since 1.0
 */
abstract class ArmyRowParamExpression extends OperationRowExpression
        implements RowParamExpression, ArmySimpleSQLExpression {

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#rowParam(TypeInfer, Collection)
     */
    static ArmyRowParamExpression multi(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingMultiParam");
        }
        return new AnonymousMultiParam(type, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer return codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#namedRowParam(TypeInfer, String, int)
     */
    static ArmyRowParamExpression named(final @Nullable TypeInfer infer, final @Nullable String name, final int size) {
        final TypeMeta type;
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw ArmyParamExpression.typeInferReturnCodecField("encodingNamedMultiParam");
        }
        return new NamedMultiParam(type, name, size);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>values is empty</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingRowParam(TypeInfer, Collection)
     */
    static ArmyRowParamExpression encodingMulti(final @Nullable TypeInfer infer, final @Nullable Collection<?> values) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (values.size() == 0) {
            throw valuesIsEmpty();
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("multiParam");
        }
        return new AnonymousMultiParam((TableField) infer, values);
    }

    /**
     * @throws CriteriaException throw when <ul>
     *                           <li>name have no text</li>
     *                           <li>size less than 1</li>
     *                           <li>infer isn't codec {@link TableField}</li>
     *                           </ul>
     * @see SQLs#encodingNamedRowParam(TypeInfer, String, int)
     */
    static ArmyRowParamExpression encodingNamed(@Nullable TypeInfer infer, @Nullable String name, final int size) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if (size < 1) {
            throw sizeLessThanOne(size);
        } else if (!(infer instanceof TableField && ((TableField) infer).codec())) {
            throw ArmyParamExpression.typeInferIsNotCodecField("namedMultiParam");
        }
        return new NamedMultiParam((TableField) infer, name, size);
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
    private ArmyRowParamExpression() {
    }

    @Override
    public final void appendSql(final StringBuilder sqlBuilder, _SqlContext context) {
        context.appendParam(this);
    }

    private static final class AnonymousMultiParam extends ArmyRowParamExpression implements MultiParam {

        private final TypeMeta type;

        final List<?> valueList;

        private AnonymousMultiParam(TypeMeta type, Collection<?> values) {
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.valueList = _Collections.asUnmodifiableList(values);
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public int columnSize() {
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
            } else if (obj instanceof AnonymousMultiParam) {
                final AnonymousMultiParam o = (AnonymousMultiParam) obj;
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
            final StringBuilder builder;
            builder = new StringBuilder((valueSize << 2) + 4)
                    .append(_Constant.SPACE_LEFT_PAREN);
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(" ?");
            }
            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//AnonymousMultiParam


    private static final class NamedMultiParam extends ArmyRowParamExpression implements NamedParam.NamedRow {

        private final String name;

        private final TypeMeta type;

        private final int valueSize;

        /**
         * @see #named(TypeInfer, String, int)
         * @see #encodingNamed(TypeInfer, String, int)
         */
        private NamedMultiParam(TypeMeta type, String name, int valueSize) {
            assert valueSize > 0;
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                assert type instanceof FieldMeta || type instanceof MappingType;
                this.type = type;
            }
            this.name = name;
            this.valueSize = valueSize;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.type;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public int columnSize() {
            return this.valueSize;
        }

        @Override
        public String toString() {
            final int valueSize = this.valueSize;
            final StringBuilder builder;
            builder = new StringBuilder((5 + this.name.length()) * valueSize + 4)
                    .append(_Constant.SPACE_LEFT_PAREN);
            for (int i = 0; i < valueSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(" ?:")
                        .append(this.name);
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
            } else if (obj instanceof NamedMultiParam) {
                final NamedMultiParam o = (NamedMultiParam) obj;
                match = o.type.equals(this.type)
                        && o.name.equals(this.name)
                        && o.valueSize == this.valueSize;
            } else {
                match = false;
            }
            return match;
        }


    }//NamedMultiParam


}
