package io.army.stmt;

public interface TwoStmtQueryStmt extends SimpleStmt,
        GenericSimpleStmt.IdSelectionIndexSpec, TwoStmtModeQuerySpec {

     int idSelectionIndex();

}
