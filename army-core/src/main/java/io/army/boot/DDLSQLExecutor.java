package io.army.boot;

import io.army.criteria.DDLSQLExecuteException;
import io.army.criteria.SQLExecutor;

import java.util.List;
import java.util.Map;

public interface DDLSQLExecutor extends SQLExecutor {

    void executeDDL(Map<String, List<String>> tableDDLMap)throws DDLSQLExecuteException;

}
