package io.army.boot.sync;

import io.army.SessionFactoryException;
import io.army.TmSessionFactory;
import io.army.boot.GenericFactoryBuilder;
import io.army.boot.SessionFactoryAdvice;
import io.army.codec.FieldCodec;
import io.army.dialect.Database;
import io.army.env.Environment;
import io.army.interceptor.DomainAdvice;

import javax.sql.XADataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TmSessionFactionBuilder extends GenericFactoryBuilder {

    @Override
    TmSessionFactionBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    @Override
    TmSessionFactionBuilder name(String sessionFactoryName);

    @Override
    TmSessionFactionBuilder environment(Environment environment);

    @Override
    TmSessionFactionBuilder factoryAdvice(List<SessionFactoryAdvice> factoryAdviceList);

    TmSessionFactionBuilder dataSource(List<XADataSource> dataSourceList);

    TmSessionFactionBuilder sqlDialectMap(Map<Integer, Database> sqlDialectMap);

    TmSessionFactionBuilder domainInterceptor(Collection<DomainAdvice> domainInterceptors);

    TmSessionFactionBuilder tableCountPerDatabase(int tableCountPerDatabase);

    TmSessionFactory build() throws SessionFactoryException;

    static TmSessionFactionBuilder builder() {
        return new TmSessionFactionBuilderImpl();
    }

}
