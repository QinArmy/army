package io.army.criteria;

public class EmptyObject {

    private static final EmptyObject INSTANCE = new EmptyObject();

    public static  EmptyObject getInstance() {
        return INSTANCE;
    }

    private EmptyObject() {
    }
}
