package io.army.criteria.impl;

import io.army.criteria.NamedParam;
import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
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
abstract class SingleParamExpression extends OperationExpression.SimpleExpression implements SQLParam {

    /**
     * @see SQLs#paramFrom(Object)
     */
    static SingleParamExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new NonNamedSingleParamExpression(_MappingFactory.getDefault(value.getClass()), value);
    }


    /**
     * @see SQLs#param(TypeInfer, Object)
     */
    static SingleParamExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NonNamedSingleParamExpression(type, value);
    }

    /**
     * @see SQLs#namedParam(TypeInfer, String)
     */
    static SingleParamExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("named parameter must have text.");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NameNonNullSingleParam(type, name);
    }

    /**
     * @see SQLs#namedNullableParam(TypeInfer, String)
     */
    static SingleParamExpression namedNullable(@Nullable TypeInfer infer, @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("named parameter must have text.");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NamedSingleParam(type, name);
    }


    final TypeMeta type;

    private SingleParamExpression(TypeMeta type) {
        this.type = type;
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
