package io.army.boot.sync;

import io.army.dialect.DDLSQLExecuteException;

import java.util.List;
import java.util.Map;

 interface DDLSQLExecutor {

     void executeDDL(Map<String, List<String>> tableDDLMap) throws DDLSQLExecuteException;

 }
