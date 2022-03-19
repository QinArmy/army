package io.army.dialect;

import io.army.bean.ObjectAccessor;
import io.army.criteria.NullHandleMode;
import io.army.criteria.impl.inner._Expression;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;

import java.util.List;
import java.util.Map;

public interface _ValueInsertContext extends _StmtContext, _InsertBlock {

    @Override
    SingleTableMeta<?> table();

    boolean migration();


    NullHandleMode nullHandle();

    Map<FieldMeta<?>, _Expression> commonExpMap();

    ObjectAccessor domainAccessor();

    List<IDomain> domainList();

    int discriminatorValue();

    @Nullable
    _InsertBlock childBlock();

}
