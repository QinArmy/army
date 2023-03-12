package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.Selection;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;

import java.util.List;

public interface _PostgreUpdate extends _SingleUpdate, _DialectStatement, _Statement._WithClauseSpec {

    @Nullable
    SQLWords modifier();

    List<_TableBlock> tableBlockList();

    /**
     * @throws UnsupportedOperationException throw when this isn't implementation of {@link io.army.criteria.DqlStatement}
     */
    List<Selection> returningList();

}
