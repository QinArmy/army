package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
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
abstract class SingleParamExpression extends OperationExpression.OperationSimpleExpression implements SQLParam {

    /**
     * @see SQLs#paramValue(Object)
     */
    static SingleParamExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        try {
            return new NonNamedSingleParamExpression(_MappingFactory.getDefault(value.getClass()), value);
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
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingParam");
        }
        return new NonNamedSingleParamExpression(type, value);
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
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedParam");
        }
        return new NameNonNullSingleParam(type, name);
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
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw nameHaveNoText();
        } else if ((type = infer.typeMeta()) instanceof TableField && ((TableField) type).codec()) {
            throw SingleParamExpression.typeInferReturnCodecField("encodingNamedNullableParam");
        }
        return new NamedSingleParam(type, name);
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
        return new NonNamedSingleParamExpression((TableField) infer, value);
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
        return new NameNonNullSingleParam((TableField) infer, name);
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
        return new NamedSingleParam((TableField) infer, name);
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


    final TypeMeta type;

    private SingleParamExpression(TypeMeta type) {
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


    private static final class NonNamedSingleParamExpression extends SingleParamExpression
            implements SingleParam, SqlValueParam.SingleNonNamedValue {

        private final Object value;

        private NonNamedSingleParamExpression(TypeMeta type, @Nullable Object value) {
            super(type);
            this.value = value;
        }

        @Override
        public Object value() {
            return this.value;
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
            } else if (obj instanceof NonNamedSingleParamExpression) {
                final NonNamedSingleParamExpression o = (NonNamedSingleParamExpression) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return " ?";
        }

    }//NonNamedSingleParamExpression


    static class NamedSingleParam extends SingleParamExpression
            implements NamedParam.NamedSingle {

        private final String name;

        private NamedSingleParam(TypeMeta type, String name) {
            super(type);
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
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
            } else if (obj instanceof NamedSingleParam) {
                final NamedSingleParam o = (NamedSingleParam) obj;
                match = o instanceof NameNonNullSingleParam == this instanceof NameNonNullSingleParam
                        && o.type == this.type
                        && o.name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            return " ?:" + this.name;
        }


    }//NamedSingleParam

    private static final class NameNonNullSingleParam extends NamedSingleParam
            implements SqlValueParam.NonNullValue {

        private NameNonNullSingleParam(TypeMeta paramMeta, String name) {
            super(paramMeta, name);
        }


    }//NameNonNullSingleParam


}
