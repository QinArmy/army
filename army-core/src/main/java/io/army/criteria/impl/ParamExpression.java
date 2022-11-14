package io.army.criteria.impl;

import io.army.criteria.NamedParam;
import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.stmt.MultiParam;
import io.army.stmt.SingleParam;

import java.util.*;

abstract class ParamExpression extends OperationExpression<TypeInfer> implements SQLParam {


    static ParamExpression single(final @Nullable TypeMeta paramMeta, final @Nullable Object value) {
        assert paramMeta != null;
        return new SingleParamExpression(paramMeta, value);
    }

    static ParamExpression multi(final @Nullable TypeMeta paramMeta, Collection<?> values) {
        assert paramMeta != null && values.size() > 0;
        return new MultiParamExpression(paramMeta, values);
    }


    static ParamExpression namedSingle(@Nullable TypeMeta paramMeta, @Nullable String name) {
        assert paramMeta != null && name != null;
        return new NameNonNullSingleParam(paramMeta, name);
    }

    static ParamExpression namedMulti(@Nullable TypeMeta paramMeta, @Nullable String name, int size) {
        assert paramMeta != null && name != null && size > 0;
        return new NamedMultiParam(paramMeta, name, size);
    }


    private ParamExpression(TypeMeta paramType) {
        super(paramType, SQLs::_identity);
    }

    @Override
    public final ParamExpression bracket() {
        //return this,don't create new instance.
        return this;
    }

    static final class SingleParamExpression extends ParamExpression
            implements SingleParam, SqlValueParam.SingleNonNamedValue {

        private final Object value;

        private SingleParamExpression(TypeMeta paramMeta, @Nullable Object value) {
            super(paramMeta);
            this.value = value;
        }

        @Override
        public Object value() {
            return this.value;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.expType, this.value);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof SingleParamExpression) {
                final SingleParamExpression o = (SingleParamExpression) obj;
                match = o.expType == this.expType && Objects.equals(o.value, this.value);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return " ?";
        }

    }//SingleParamExpression


    static final class MultiParamExpression extends ParamExpression
            implements MultiParam, SqlValueParam.MultiValue, MultiValueExpression {

        private final List<?> valueList;

        private MultiParamExpression(TypeMeta paramMeta, Collection<?> values) {
            super(paramMeta);
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
        public void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }


        @Override
        public void appendSqlWithParens(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            this.appendSql(context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.expType, this.valueList);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof MultiParamExpression) {
                final MultiParamExpression o = (MultiParamExpression) obj;
                match = o.expType == this.expType
                        && o.valueList.equals(this.valueList);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            final int size = this.valueList.size();
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(" ?");
            }
            return builder.toString();
        }


    }//MultiParamExpression


    static class NamedSingleParam extends ParamExpression
            implements NamedParam.NamedSingle {

        private final String name;

        private NamedSingleParam(TypeMeta paramMeta, String name) {
            super(paramMeta);
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

        @Override
        public final void appendSql(final _SqlContext context) {
            context.appendParam(this);
        }


        @Override
        public final int hashCode() {
            return Objects.hash(this.expType, this.name);
        }

        @Override
        public final boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedSingleParam) {
                final NamedSingleParam o = (NamedSingleParam) obj;
                match = o.expType == this.expType && o.name.equals(this.name);
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

    static final class NamedMultiParam extends ParamExpression
            implements NamedParam.NamedMulti, MultiValueExpression {

        private final String name;

        private final int valueSize;

        private NamedMultiParam(TypeMeta paramMeta, String name, int valueSize) {
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
            context.appendParam(this);
        }

        @Override
        public void appendSqlWithParens(final _SqlContext context) {
            final StringBuilder sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);
            context.appendParam(this);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.expType, this.name, this.valueSize);
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof NamedMultiParam) {
                final NamedMultiParam o = (NamedMultiParam) obj;
                match = o.expType == this.expType
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
                        .append('[')
                        .append(i)
                        .append(']');
            }
            return builder.append(_Constant.LEFT_PAREN)
                    .toString();
        }


    }//NameMultiParam


}
