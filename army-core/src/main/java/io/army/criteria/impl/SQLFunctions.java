package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.mapping.MappingType;

import java.util.List;

abstract class SQLFunctions extends OperationExpression implements Expression {


    static Expression noArgumentFunc(String name, MappingType returnType) {
        // return new NoArgumentFunc<>(name, returnType);
        return null;
    }

    static Expression oneArgumentFunc(String name, MappingType returnType, Expression one) {
        // return new OneArgumentFunc<>(name, returnType, (_Expression) one);
        return null;
    }

    static Expression twoArgumentFunc(String name, MappingType returnType, Expression one
            , Expression two) {
        //  return new TwoArgumentFunc<>(name, returnType, (_Expression) one, (_Expression) two);
        return null;
    }

    static Expression twoArgumentFunc(String name, MappingType returnType, List<String> format
            , Expression one, Expression two) {
        //  return new TwoArgumentFunc<>(name, returnType, format, (_Expression) one, (_Expression) two);
        return null;
    }

//
//    private final String name;
//
//    protected final MappingType returnType;
//
//    private SQLFunctions(String name, MappingType returnType) {
//        this.name = name;
//        this.returnType = returnType;
//    }
//
//    @Override
//    public final MappingType mappingType() {
//        return returnType;
//    }
//
//
//    @Override
//    public final void appendSql(_SqlContext context) {
//        StringBuilder builder = context.sqlBuilder()
//                .append(" ")
//                .append(this.name)
//                .append("(");
//        doAppendArgument(context);
//        builder.append(")");
//    }
//
//    @Override
//    public final boolean containsSubQuery() {
//        return false;
//    }
//
//    @Override
//    public final ParamMeta paramMeta() {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public final String toString() {
//        return String.format("%s(%s)", name, argumentToString());
//    }
//
//    protected abstract void doAppendArgument(_SqlContext context);
//
//    protected abstract String argumentToString();
//
//    /*################################## blow static class  ##################################*/
//
//    private static final class NoArgumentFunc extends SQLFunctions {
//
//        NoArgumentFunc(String name, MappingType returnType) {
//            super(name, returnType);
//        }
//
//        @Override
//        protected void doAppendArgument(_SqlContext context) {
//
//        }
//
//        @Override
//        protected String argumentToString() {
//            return "";
//        }
//
//    }
//
//    static final class OneArgumentFunc extends SQLFunctions {
//
//        private final _Expression one;
//
//        private final List<MappingType> argumentTypeList;
//
//        private OneArgumentFunc(String name, MappingType returnType, _Expression one) {
//            super(name, returnType);
//            this.one = one;
//            this.argumentTypeList = Collections.singletonList(one.mappingType());
//        }
//
//        @Override
//        protected void doAppendArgument(_SqlContext context) {
//
//        }
//
//        @Override
//        protected String argumentToString() {
//            return one.toString();
//        }
//
//    }
//
//    static class TwoArgumentFunc extends SQLFunctions {
//
//        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", "");
//
//        private final List<String> format;
//
//        private final _Expression one;
//
//        private final _Expression two;
//
//        private final List<MappingType> argumentTypeList;
//
//        private TwoArgumentFunc(String name, MappingType returnType, List<String> format, _Expression one
//                , _Expression two) {
//            super(name, returnType);
//            _Assert.isTrue(format.size() >= 3, "");
//            this.format = format;
//            this.one = one;
//            this.two = two;
//
//            List<MappingType> typeList = new ArrayList<>(2);
//            typeList.add(one.mappingType());
//            typeList.add(two.mappingType());
//            this.argumentTypeList = Collections.unmodifiableList(typeList);
//        }
//
//        private TwoArgumentFunc(String name, MappingType returnType, _Expression one, _Expression two) {
//            this(name, returnType, FORMAT_LIST, one, two);
//        }
//
//        @Override
//        protected void doAppendArgument(_SqlContext context) {
//            StringBuilder builder = context.sqlBuilder();
//            builder.append(format.get(0));
//            one.appendSql(context);
//            builder.append(format.get(1));
//            two.appendSql(context);
//            builder.append(format.get(2));
//        }
//
//        @Override
//        protected String argumentToString() {
//            return format.get(0) + one + format.get(1) + two + format.get(2);
//        }
//
//    }
//
//    static final class ThreeArgumentFunc extends SQLFunctions {
//
//        private static final List<String> FORMAT_LIST = ArrayUtils.asUnmodifiableList("", ",", ",", "");
//
//        private final List<String> format;
//
//        private final _Expression one;
//
//        private final _Expression two;
//
//        protected final _Expression three;
//
//        private final List<MappingType> argumentTypeList;
//
//        ThreeArgumentFunc(String name, MappingType returnType, List<String> format, Expression one
//                , Expression two, Expression three) {
//            super(name, returnType);
//            _Assert.isTrue(format.size() >= 4, "showSQL error");
//            this.format = format;
//            this.one = (_Expression) one;
//            this.two = (_Expression) two;
//            this.three = (_Expression) three;
//
//            List<MappingType> typeList = new ArrayList<>(3);
//            typeList.add(one.mappingType());
//            typeList.add(two.mappingType());
//            typeList.add(three.mappingType());
//            this.argumentTypeList = Collections.unmodifiableList(typeList);
//        }
//
//        ThreeArgumentFunc(String name, MappingType returnType, Expression one
//                , Expression two, Expression three) {
//            this(name, returnType, FORMAT_LIST, one, two, three);
//        }
//
//
//        @Override
//        protected void doAppendArgument(_SqlContext context) {
//            StringBuilder builder = context.sqlBuilder();
//            builder.append(format.get(0));
//            one.appendSql(context);
//            builder.append(format.get(1));
//            two.appendSql(context);
//            builder.append(format.get(2));
//            three.appendSql(context);
//            builder.append(format.get(3));
//        }
//
//        @Override
//        protected String argumentToString() {
//            return format.get(0) + one + format.get(1) + two + format.get(2) + three + format.get(3);
//        }
//
//    }


}
