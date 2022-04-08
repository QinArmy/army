package io.army.dialect;

import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Update;
import io.army.lang.Nullable;
import io.army.meta.SingleTableMeta;
import io.army.stmt.SimpleStmt;

import java.util.List;

public interface _SingleDeleteContext extends _DeleteContext, _Block {

    @Override
    SingleTableMeta<?> table();

    /**
     * @see _Update#predicateList()
     */
    List<_Predicate> predicateList();

    boolean multiTableUpdateChild();

    @Nullable
    _Block childBlock();

    @Override
    SimpleStmt build();
}
