package io.army.stmt;

public interface DeclareCursorStmt extends SimpleStmt {

    String cursorName();

    String safeCursorName();

}
