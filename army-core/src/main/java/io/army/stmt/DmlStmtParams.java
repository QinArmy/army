package io.army.stmt;

public interface DmlStmtParams extends StmtParams {

    /**
     * @return <ul>
     * <li>If {@link  #selectionList()} exists id and executing need the index of id selection.</li>
     * <li>Else negative</li>
     * </ul>
     * @see InsertStmtParams#idSelectionIndex()
     * @see GeneratedKeyStmt#idSelectionIndex()
     * @see TwoStmtModeQueryStmt#idSelectionIndex()
     */
    int idSelectionIndex();


}
