package io.army.boot.sync;

import io.army.SessionFactoryException;
import io.army.advice.sync.DomainAdvice;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.env.ArmyEnvironment;
import io.army.sync.SessionFactoryAdvice;
import io.army.sync.TmSessionFactory;

import javax.sql.XADataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TmSessionFactionBuilder extends SyncSessionFactoryBuilder {

    @Override
    TmSessionFactionBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    @Override
    TmSessionFactionBuilder name(String sessionFactoryName);

    @Override
    TmSessionFactionBuilder environment(ArmyEnvironment environment);

    @Override
    TmSessionFactionBuilder factoryAdvice(Collection<SessionFactoryAdvice> factoryAdvices);

    @Override
    TmSessionFactionBuilder tableCountPerDatabase(int tableCountPerDatabase);

    @Override
    TmSessionFactionBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors);

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
