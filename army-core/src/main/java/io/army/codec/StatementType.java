package io.army.codec;

public enum StatementType {

    INSERT,
    UPDATE,
    DELETE,
    SELECT,
    WITH_INSERT,
    WITH_UPDATE,
    WITH_DELETE;

    public boolean insertStatement() {
        return this == INSERT
                || this == WITH_INSERT;
    }
}
