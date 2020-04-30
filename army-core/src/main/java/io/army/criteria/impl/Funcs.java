package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FuncExpression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingType;
import io.army.util.ArrayUtils;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class Funcs<E> extends AbstractExpression<E> implements FuncExpression<E> {

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
    public final String name() {
        return this.name;
    }

    @Override
    protected void afterSpace(SQLContext context) {
        context.sqlBuilder()
                .append(name)
                .append("(");
        doAppendArgument(context);
        context.sqlBuilder().append(")");
    }

    @Override
    public final String beforeAs() {
        return String.format("%s(%s)", name, argumentToString());
    }

    protected abstract void doAppendArgument(SQLContext context);

    protected abstract String argumentToString();

    /*################################## blow static class  ##################################*/

    static final class NoArgumentFunc<E> extends Funcs<E> {

        NoArgumentFunc(String name, MappingType returnType) {
            super(name, returnType);
        }

        @Override
        protected void doAppendArgument(SQLContext context) {

        }

        @Override
        protected String argumentToString() {
            return "";
        }

        @Override
        public List<MappingType> argumentTypeList() {
            return Collections.emptyList();
        }
    }

    static final class OneArgumentFunc<E> extends Funcs<E> {

        private final Expression<?> one;

        private final List<MappingType> argumentTypeList;

        OneArgumentFunc(String name, MappingType returnType, Expression<?> one) {
            super(name, returnType);
            this.one = one;
            this.argumentTypeList = Collections.singletonList(one.mappingType());
        }

        @Override
        protected void doAppendArgument(SQLContext context) {
            one.appendSQL(context);
        }

        @Override
        protected String argumentToString() {
            return one.toString();
        }

        @Override
        public List<MappingType> argumentTypeList() {
            return this.argumentTypeList;
        }
    }

    static class TwoArgumentFunc<E> extends Funcs<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", "");

        private final List<String> format;

        private final Expression<?> one;

        private final Expression<?> two;

        private final List<MappingType> argumentTypeList;

        TwoArgumentFunc(String name, MappingType returnType, List<String> format, Expression<?> one
                , Expression<?> two) {
            super(name, returnType);
            Assert.isTrue(format.size() >= 3, "");
            this.format = format;
            this.one = one;
            this.two = two;

            List<MappingType> typeList = new ArrayList<>(2);
            typeList.add(one.mappingType());
            typeList.add(two.mappingType());
            this.argumentTypeList = Collections.unmodifiableList(typeList);
        }

        TwoArgumentFunc(String name, MappingType returnType, Expression<?> one, Expression<?> two) {
            this(name, returnType, FORMAT_LIST, one, two);
        }

        @Override
        protected void doAppendArgument(SQLContext context) {
            StringBuilder builder = context.sqlBuilder();
            builder.append(format.get(0));
            one.appendSQL(context);
            builder.append(format.get(1));
            two.appendSQL(context);
            builder.append(format.get(2));
        }

        @Override
        protected String argumentToString() {
            return format.get(0) + one + format.get(1) + two + format.get(2);
        }

        @Override
        public List<MappingType> argumentTypeList() {
            return this.argumentTypeList;
        }
    }

    static final class ThreeArgumentFunc<E> extends Funcs<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", ",", "");

        private final List<String> format;

        private final Expression<?> one;

        private final Expression<?> two;

        protected final Expression<?> three;

        private final List<MappingType> argumentTypeList;

        ThreeArgumentFunc(String name, MappingType returnType, List<String> format, Expression<?> one
                , Expression<?> two, Expression<?> three) {
            super(name, returnType);
            Assert.isTrue(format.size() >= 4, "format error");
            this.format = format;
            this.one = one;
            this.two = two;
            this.three = three;

            List<MappingType> typeList = new ArrayList<>(3);
            typeList.add(one.mappingType());
            typeList.add(two.mappingType());
            typeList.add(three.mappingType());
            this.argumentTypeList = Collections.unmodifiableList(typeList);
        }

        ThreeArgumentFunc(String name, MappingType returnType, Expression<?> one
                , Expression<?> two, Expression<?> three) {
            this(name, returnType, FORMAT_LIST, one, two, three);
        }


        @Override
        protected void doAppendArgument(SQLContext context) {
            StringBuilder builder = context.sqlBuilder();
            builder.append(format.get(0));
            one.appendSQL(context);
            builder.append(format.get(1));
            two.appendSQL(context);
            builder.append(format.get(2));
            three.appendSQL(context);
            builder.append(format.get(3));
        }

        @Override
        protected String argumentToString() {
            return format.get(0) + one + format.get(1) + two + format.get(2) + three + format.get(3);
        }

        @Override
        public List<MappingType> argumentTypeList() {
            return this.argumentTypeList;
        }
    }


}
