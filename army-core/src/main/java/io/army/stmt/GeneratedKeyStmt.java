package io.army.stmt;

import io.army.meta.PrimaryFieldMeta;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface GeneratedKeyStmt extends SimpleStmt {

    int rowSize();

    void setGeneratedIdValue(int indexBasedZero, Object idValue);

    PrimaryFieldMeta<?> idField();

    String idReturnAlias();

}
