package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;
import io.army.util.ArrayUtils;
import io.army.util.Assert;

import java.util.List;

abstract class Funcs<E> extends AbstractExpression<E> {

    private final String name;

    protected final MappingType returnType;

    Funcs(String name, MappingType returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    @Override
    public final MappingType mappingType() {
        return returnType;
    }

    @Override
    protected void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append(name)
                .append("(");
        doAppendArgument(sql,builder, paramWrapperList);
        builder.append(")");
    }


    protected abstract void doAppendArgument(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList);

    /*################################## blow static class  ##################################*/

    static class NoArgumentFunc<E> extends Funcs<E> {

        NoArgumentFunc(String name, MappingType returnType) {
            super(name, returnType);
        }

        @Override
        protected void doAppendArgument(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {

        }
    }

    static class OneArgumentFunc<E> extends Funcs<E> {

        protected final Expression<?> one;

        OneArgumentFunc(String name, MappingType returnType, Expression<?> one) {
            super(name, returnType);
            this.one = one;
        }

        @Override
        protected void doAppendArgument(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
            one.appendSQL(sql,builder, paramWrapperList);
        }

    }

    static class TwoArgumentFunc<E> extends OneArgumentFunc<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", "");

        protected final List<String> format;


        protected final Expression<?> two;

        TwoArgumentFunc(String name, MappingType returnType, List<String> format, Expression<?> one
                , Expression<?> two) {
            super(name, returnType, one);
            Assert.isTrue(format.size() > 2, "");
            this.format = format;
            this.two = two;
        }

        TwoArgumentFunc(String name, MappingType returnType, Expression<?> one, Expression<?> two) {
            this(name, returnType, FORMAT_LIST, one, two);
        }

        @Override
        protected void doAppendArgument(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
            builder.append(format.get(0));
            one.appendSQL(sql,builder, paramWrapperList);
            builder.append(format.get(1));
            two.appendSQL(sql,builder, paramWrapperList);
            builder.append(format.get(2));
        }

    }

    static class ThreeArgumentFunc<E> extends TwoArgumentFunc<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", ",", "");

        protected final Expression<?> three;

        ThreeArgumentFunc(String name, MappingType returnType, List<String> format, Expression<?> one
                , Expression<?> two, Expression<?> three) {
            super(name, returnType, format, one, two);
            this.three = three;
        }

        ThreeArgumentFunc(String name, MappingType returnType, Expression<?> one
                , Expression<?> two, Expression<?> three) {
            super(name, returnType, FORMAT_LIST, one, two);
            this.three = three;
        }

        @Override
        protected void doAppendArgument(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
            builder.append(format.get(0));
            one.appendSQL(sql,builder, paramWrapperList);
            builder.append(format.get(1));
            two.appendSQL(sql,builder, paramWrapperList);
            builder.append(format.get(2));
            three.appendSQL(sql,builder, paramWrapperList);
            builder.append(format.get(3));

        }

    }


}
