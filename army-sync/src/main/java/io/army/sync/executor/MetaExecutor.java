package io.army.sync.executor;


import io.army.schema.SchemaInfo;
import io.army.session.DataAccessException;

import java.util.List;

public interface MetaExecutor extends AutoCloseable {


    SchemaInfo extractInfo() throws DataAccessException;

    void executeDdl(List<String> ddlList) throws DataAccessException;

    @Override
    void close() throws DataAccessException;

}
