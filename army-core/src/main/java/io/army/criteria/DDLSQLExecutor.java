package io.army.criteria;

import java.util.List;
import java.util.Map;

public interface DDLSQLExecutor extends SQLExecutor {

    void executeDDL(Map<String, List<String>> tableDDLMap)throws DDLSQLExecuteException;

}
