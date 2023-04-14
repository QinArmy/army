package io.army.criteria.impl;

import io.army.criteria.NamedLiteral;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.Objects;


/**
 * <p>
 * This class representing literal expression,{@link  SingleParamExpression} and {@link SingleLiteralExpression}
 * must extends {@link OperationExpression } not {@link Expressions}.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleLiteralExpression extends OperationExpression.SimpleExpression {

    /**
     * @see SQLs#literalFrom(Object)
     */
    static SingleLiteralExpression from(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return new NonNamedSingleLiteral(_MappingFactory.getDefault(value.getClass()), value);
    }


    /**
     * @see SQLs#literal(TypeInfer, Object)
     * @see SQLs#literalFrom(Object)
     */
    static SingleLiteralExpression single(final @Nullable TypeInfer infer, final @Nullable Object value) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NonNamedSingleLiteral(type, value);
    }

    /**
     * @see SQLs#namedLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression named(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("named single-literal must have text.");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NamedNonNullSingleLiteral(type, name);
    }

    /**
     * @see SQLs#namedNullableLiteral(TypeInfer, String)
     */
    static SingleLiteralExpression namedNullable(final @Nullable TypeInfer infer, final @Nullable String name) {
        if (infer == null) {
            throw ContextStack.clearStackAndNullPointer();
        } else if (!_StringUtils.hasText(name)) {
            throw ContextStack.clearStackAndCriteriaError("named single-literal must have text.");
        }
        final TypeMeta type;
        if (infer instanceof TypeMeta) {
            type = (TypeMeta) infer;
        } else {
            type = infer.typeMeta();
        }
        return new NamedSingleLiteral(type, name);
    }


    final TypeMeta type;

    private SingleLiteralExpression(TypeMeta type) {
        this.type = type;
    }

    @Override
    public final TypeMeta typeMeta() {
        return this.type;
    }


    static final class NonNamedSingleLiteral extends SingleLiteralExpression
            implements SqlValueParam.SingleNonNamedValue {

        private final Object value;

        private NonNamedSingleLiteral(TypeMeta paramMeta, @Nullable Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendLiteral(this.type, value);
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
            } else if (obj instanceof NonNamedSingleLiteral) {
                final NonNamedSingleLiteral o = (NonNamedSingleLiteral) obj;
                match = o.type.equals(this.type)
                        && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final String s;
            if (this.value == null) {
                s = _Constant.SPACE_NULL;
            } else {
                s = " " + this.value;
            }
            return s;
        }


    }//NonNamedSingleLiteral

    static class NamedSingleLiteral extends SingleLiteralExpression implements NamedLiteral,
            SqlValueParam.SingleValue {

        private final String name;

        private NamedSingleLiteral(TypeMeta type, String name) {
            super(type);
            this.name = name;
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
        public final int hashCode() {
            return Objects.hash(this.type, this.name);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedSingleLiteral) {
                final NamedSingleLiteral o = (NamedSingleLiteral) obj;
                match = o instanceof NamedNonNullSingleLiteral == this instanceof NamedNonNullSingleLiteral
                        && o.type.equals(this.type)
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


    }//NamedSingleLiteral

    private static final class NamedNonNullSingleLiteral extends NamedSingleLiteral
            implements SqlValueParam.NonNullValue {

        private NamedNonNullSingleLiteral(TypeMeta type, String name) {
            super(type, name);
        }


    }//NamedNonNullSingleLiteral


}
