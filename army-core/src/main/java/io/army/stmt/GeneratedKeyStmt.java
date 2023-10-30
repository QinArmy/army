package io.army.stmt;

import io.army.meta.PrimaryFieldMeta;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface GeneratedKeyStmt extends SimpleStmt, SingleSqlStmt.IdSelectionIndexSpec {

    int rowSize();

    void setGeneratedIdValue(int indexBasedZero, Object idValue);

    PrimaryFieldMeta<?> idField();

    /**
     * @return <ul>
     * <li>If {@link #selectionList()} is empty, negative</li>
     * <li>Else index</li>
     * </ul>
     */
    int idSelectionIndex();


}
