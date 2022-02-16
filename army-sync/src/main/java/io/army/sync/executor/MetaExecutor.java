package io.army.sync.executor;


import io.army.schema._SchemaInfo;
import io.army.session.DataAccessException;

public interface MetaExecutor extends Executor {


    _SchemaInfo extractInfo() throws DataAccessException;


}
