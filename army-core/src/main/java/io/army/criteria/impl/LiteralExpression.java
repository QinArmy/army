package io.army.criteria.impl;

import io.army.criteria.NamedLiteral;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * <p>
 * This class representing sql literal expression.
 * </p>
 */
abstract class LiteralExpression extends OperationExpression<TypeInfer> {

    static LiteralExpression single(final @Nullable TypeMeta paramMeta, final @Nullable Object constant) {
        assert paramMeta != null;
        return new SingleLiteral(paramMeta, constant);
    }

    static LiteralExpression multi(final @Nullable TypeMeta paramMeta, final Collection<?> values) {
        assert paramMeta != null && values.size() > 0;
        return new MultiLiteralExpression(paramMeta, values);
    }

    static LiteralExpression namedNullableSingle(final @Nullable TypeMeta paramMeta, final @Nullable String name) {
        assert paramMeta != null && name != null;
        return new NamedSingleLiteral(paramMeta, name);
    }

    static LiteralExpression namedSingle(final @Nullable TypeMeta paramMeta, final @Nullable String name) {
        assert paramMeta != null && name != null;
        return new NonNullNamedSingleLiteral(paramMeta, name);
    }

    static LiteralExpression namedMulti(final @Nullable TypeMeta paramMeta, final @Nullable String name, int size) {
        assert paramMeta != null && name != null && size > 0;
        return new NamedMultiLiteral(paramMeta, name, size);
    }


    static void appendMultiLiteral(final _SqlContext context, final TypeMeta paramMeta, final List<?> valueList) {

        final int size = valueList.size();
        assert size > 0;

        final StringBuilder sqlBuilder = context.sqlBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                sqlBuilder.append(_Constant.SPACE);
            }
            context.appendLiteral(paramMeta, valueList.get(i));
        }
    }

    static StringBuilder multiLiteralToString(final StringBuilder builder, final List<?> valueList) {
        final int size = valueList.size();

        builder.append(_Constant.SPACE_LEFT_PAREN);
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(_Constant.SPACE_COMMA_SPACE);
            } else {
                builder.append(_Constant.SPACE);
            }
            builder.append(valueList.get(i));
        }
        return builder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    final TypeMeta typeMeta;


    private LiteralExpression(TypeMeta typeMeta) {
        super(SQLs::_identity);
        this.typeMeta = typeMeta;
    }


    @Override
    public final TypeMeta typeMeta() {
        return this.typeMeta;
    }

    @Override
    public final LiteralExpression bracket() {
        //return this,don't create new instance.
        return this;
    }


    static final class SingleLiteral extends LiteralExpression
            implements SqlValueParam.SingleNonNamedValue {

        private final Object value;

        private SingleLiteral(TypeMeta paramMeta, @Nullable Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final Object value = this.value;
            if (value == null) {
                context.sqlBuilder().append(_Constant.SPACE_NULL);
            } else {
                context.appendLiteral(this.typeMeta, value);
            }

        }

        @Override
        public Object value() {
            return this.value;
        }


        @Override
        public int hashCode() {
            return Objects.hash(this.typeMeta, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SingleLiteral) {
                final SingleLiteral o = (SingleLiteral) obj;
                match = o.typeMeta == this.typeMeta && Objects.equals(o.value, this.value);
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


     }//SingleLiteralExpression


     static final class MultiLiteralExpression extends LiteralExpression
             implements MultiValueExpression {

         private final List<?> valueList;

         private MultiLiteralExpression(TypeMeta paramMeta, Collection<?> valueList) {
             super(paramMeta);
             this.valueList = _CollectionUtils.asUnmodifiableList(valueList);
             assert this.valueList.size() > 0;
         }

        @Override
        public void appendSql(final _SqlContext context) {
            appendMultiLiteral(context, this.typeMeta, this.valueList);
        }

        @Override
        public void appendSqlWithParens(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            appendMultiLiteral(context, this.typeMeta, this.valueList);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public int valueSize() {
            return this.valueList.size();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.typeMeta, this.valueList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof MultiLiteralExpression) {
                final MultiLiteralExpression o = (MultiLiteralExpression) obj;
                match = o.typeMeta == this.typeMeta && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return multiLiteralToString(new StringBuilder(), this.valueList)
                    .toString();
        }


    }//MultiLiteralExpression


    static class NamedSingleLiteral extends LiteralExpression
            implements NamedLiteral, SqlValueParam.SingleValue {

        private final String name;

        private NamedSingleLiteral(TypeMeta paramMeta, String name) {
            super(paramMeta);
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
            return Objects.hash(this.typeMeta, this.name);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedSingleLiteral) {
                final NamedSingleLiteral o = (NamedSingleLiteral) obj;
                match = o.typeMeta == this.typeMeta && o.name.equals(this.name);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public final String toString() {
            return String.format(" ?{%s}", this.name);
        }


    }//NamedSingleLiteral

    private static final class NonNullNamedSingleLiteral extends NamedSingleLiteral
            implements SqlValueParam.NonNullValue {

        private NonNullNamedSingleLiteral(TypeMeta paramMeta, String name) {
            super(paramMeta, name);
        }


    }//NonNullNamedSingleLiteral


    static final class NamedMultiLiteral extends LiteralExpression
            implements NamedLiteral, SqlValueParam.NamedMultiValue, MultiValueExpression {

        private final String name;

        private final int valueSize;

        private NamedMultiLiteral(TypeMeta paramMeta, String name, int valueSize) {
            super(paramMeta);
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
        public void appendSqlWithParens(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            context.appendLiteral(this);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.typeMeta, this.name, this.valueSize);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedMultiLiteral) {
                final NamedMultiLiteral o = (NamedMultiLiteral) obj;
                match = o.typeMeta == this.typeMeta
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
            final StringBuilder builder = new StringBuilder()
                    .append(_Constant.LEFT_PAREN);


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

            return builder.append(_Constant.LEFT_PAREN)
                    .toString();
        }


    }//NamedMultiLiteral


}
