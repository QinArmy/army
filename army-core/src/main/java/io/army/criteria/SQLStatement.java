package io.army.criteria;

public enum SQLStatement {

    UPDATE,
    OBJECT_UPDATE,
    MULTI_UPDATE,
    DELETE,
    MULTI_DELETE,
    OBJECT_DELETE,
    SELECT;




    public boolean isUpdate() {
        return this == UPDATE
                || this == OBJECT_UPDATE;
    }
}
