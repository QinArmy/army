package io.army.boot.sync;

import io.army.SessionFactoryException;
import io.army.dialect.Database;
import io.army.sync.TmSessionFactory;

import javax.sql.XADataSource;
import java.util.List;
import java.util.Map;

public interface TmSessionFactionBuilder extends SyncSessionFactoryBuilder<TmSessionFactionBuilder> {


    TmSessionFactionBuilder dataSourceList(List<XADataSource> dataSourceList);

    TmSessionFactionBuilder databaseMap(Map<Integer, Database> databaseMap);

    TmSessionFactory build() throws SessionFactoryException;

    static TmSessionFactionBuilder builder() {
        return builder(false);
    }

    static TmSessionFactionBuilder builder(boolean springApplication) {
        return TmSessionFactionBuilderImpl.buildInstance(springApplication);
    }


}
