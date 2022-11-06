package io.army.stmt;

import java.util.List;

public interface MultiStmt {

    String multiSql();

    List<ResultItem> resultItemList();


}
