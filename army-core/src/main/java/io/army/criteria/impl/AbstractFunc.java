package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.FuncExpression;
import io.army.criteria.SQLContext;
import io.army.meta.mapping.MappingMeta;
import io.army.util.ArrayUtils;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class AbstractFunc<E> extends AbstractExpression<E> implements FuncExpression<E> {


    static <E> FuncExpression<E> noArgumentFunc(String name, MappingMeta returnType) {
        return new NoArgumentFunc<>(name, returnType);
    }

    static <E> FuncExpression<E> oneArgumentFunc(String name, MappingMeta returnType, Expression<?> one) {
        return new OneArgumentFunc<>(name, returnType, one);
    }

    static <E> FuncExpression<E> twoArgumentFunc(String name, MappingMeta returnType, Expression<?> one
            , Expression<?> two) {
        return new TwoArgumentFunc<>(name, returnType, one, two);
    }

    static <E> FuncExpression<E> twoArgumentFunc(String name, MappingMeta returnType, List<String> format
            , Expression<?> one, Expression<?> two) {
        return new TwoArgumentFunc<>(name, returnType, format, one, two);
    }


    private final String name;

    protected final MappingMeta returnType;

    private AbstractFunc(String name, MappingMeta returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    @Override
    public final MappingMeta mappingMeta() {
        return returnType;
    }

    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(this.name)
                .append("(");
        doAppendArgument(context);
        builder.append(")");
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    @Override
    public final String toString() {
        return String.format("%s(%s)", name, argumentToString());
    }

    protected abstract void doAppendArgument(SQLContext context);

    protected abstract String argumentToString();

    /*################################## blow static class  ##################################*/

    private static final class NoArgumentFunc<E> extends AbstractFunc<E> {

        NoArgumentFunc(String name, MappingMeta returnType) {
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
        public List<MappingMeta> argumentTypeList() {
            return Collections.emptyList();
        }
    }

    static final class OneArgumentFunc<E> extends AbstractFunc<E> {

        private final Expression<?> one;

        private final List<MappingMeta> argumentTypeList;

        private OneArgumentFunc(String name, MappingMeta returnType, Expression<?> one) {
            super(name, returnType);
            this.one = one;
            this.argumentTypeList = Collections.singletonList(one.mappingMeta());
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
        public List<MappingMeta> argumentTypeList() {
            return this.argumentTypeList;
        }
    }

    static class TwoArgumentFunc<E> extends AbstractFunc<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", "");

        private final List<String> format;

        private final Expression<?> one;

        private final Expression<?> two;

        private final List<MappingMeta> argumentTypeList;

        private TwoArgumentFunc(String name, MappingMeta returnType, List<String> format, Expression<?> one
                , Expression<?> two) {
            super(name, returnType);
            Assert.isTrue(format.size() >= 3, "");
            this.format = format;
            this.one = one;
            this.two = two;

            List<MappingMeta> typeList = new ArrayList<>(2);
            typeList.add(one.mappingMeta());
            typeList.add(two.mappingMeta());
            this.argumentTypeList = Collections.unmodifiableList(typeList);
        }

        private TwoArgumentFunc(String name, MappingMeta returnType, Expression<?> one, Expression<?> two) {
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
        public List<MappingMeta> argumentTypeList() {
            return this.argumentTypeList;
        }
    }

    static final class ThreeArgumentFunc<E> extends AbstractFunc<E> {

        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", ",", "");

        private final List<String> format;

        private final Expression<?> one;

        private final Expression<?> two;

        protected final Expression<?> three;

        private final List<MappingMeta> argumentTypeList;

        ThreeArgumentFunc(String name, MappingMeta returnType, List<String> format, Expression<?> one
                , Expression<?> two, Expression<?> three) {
            super(name, returnType);
            Assert.isTrue(format.size() >= 4, "showSQL error");
            this.format = format;
            this.one = one;
            this.two = two;
            this.three = three;

            List<MappingMeta> typeList = new ArrayList<>(3);
            typeList.add(one.mappingMeta());
            typeList.add(two.mappingMeta());
            typeList.add(three.mappingMeta());
            this.argumentTypeList = Collections.unmodifiableList(typeList);
        }

        ThreeArgumentFunc(String name, MappingMeta returnType, Expression<?> one
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
        public List<MappingMeta> argumentTypeList() {
            return this.argumentTypeList;
        }
    }


}
