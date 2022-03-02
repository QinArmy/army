package io.army.sync.executor;


import io.army.schema._SchemaInfo;
import io.army.session.DataAccessException;

import java.util.List;

public interface MetaExecutor extends Executor {


    _SchemaInfo extractInfo() throws DataAccessException;

    void executeDdl(List<String> ddlList) throws DataAccessException;

}
