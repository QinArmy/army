package io.army.stmt;

public enum StmtType {

    /**
     * readonly statement
     */
    QUERY,

    /**
     * insert statement that need return id.
     */
    INSERT,

    /**
     * update statement,contain the insert statement that don't need return id.
     */
    UPDATE
}
