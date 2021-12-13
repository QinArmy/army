package io.army.criteria;

@Deprecated
public class EmptyObject {

    static final EmptyObject INSTANCE = new EmptyObject();

    public static EmptyObject getInstance() {
        return INSTANCE;
    }

    private EmptyObject() {
    }
}
