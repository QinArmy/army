package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;
import java.util.function.BiConsumer;

public interface InsertStmtParams extends StmtParams {


    @Nullable
    PrimaryFieldMeta<?> idField();

    @Nullable
    String idReturnAlias();


    interface DomainParams extends InsertStmtParams {

        List<IDomain> domainList();

        ObjectAccessor domainAccessor();

    }


    interface ValueParams extends InsertStmtParams {

        List<BiConsumer<Integer, Object>> consumerList();

    }


}
