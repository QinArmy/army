package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.lang.Nullable;

import java.util.List;

public interface _PostgreDelete extends _SingleDelete, _DialectStatement, _Statement._WithClauseSpec,
        _Statement._ReturningListSpec {

    @Nullable
    SQLWords modifier();

    List<_TableBlock> tableBlockList();


}
