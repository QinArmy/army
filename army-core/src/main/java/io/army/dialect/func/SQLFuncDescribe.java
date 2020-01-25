package io.army.dialect.func;

public interface SQLFuncDescribe {

    String name();

    boolean hasArguments();

    static SQLFuncDescribe build(String name,boolean hasArguments){
        return new SQLFuncDescribeImpl(name,hasArguments);
    }

}
