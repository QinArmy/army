package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;

public interface InsertStmtParams extends StmtParams {

    List<IDomain> domainList();

    ObjectAccessor domainAccessor();

    @Nullable
    PrimaryFieldMeta<?> returnId();

    @Nullable
    String idReturnAlias();


}
