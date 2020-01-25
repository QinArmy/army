package io.army.dialect.func;

class SQLFuncDescribeImpl implements SQLFuncDescribe {

    private final String name;

    private final boolean hasArguments;


    SQLFuncDescribeImpl(String name, boolean hasArguments) {
        this.name = name;
        this.hasArguments = hasArguments;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean hasArguments() {
        return hasArguments;
    }

}
