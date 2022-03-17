package io.army.dialect;

import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.Map;

public interface _ValueInsertContext extends _StmtContext, _InsertBlock {

    @Override
    SingleTableMeta<?> table();

    NullHandleMode nullHandle();

    Map<FieldMeta<?>, _Expression> commonExpMap();

    List<? extends ReadWrapper> domainList();

    int discriminatorValue();

    @Nullable
    _InsertBlock childBlock();

}
