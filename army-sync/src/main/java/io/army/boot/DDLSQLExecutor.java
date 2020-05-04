package io.army.boot;

import io.army.dialect.DDLSQLExecuteException;

import java.util.List;
import java.util.Map;

 interface DDLSQLExecutor extends SQLExecutor {

    void executeDDL(Map<String, List<String>> tableDDLMap) throws DDLSQLExecuteException;

 }
