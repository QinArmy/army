package io.army.criteria.inner.postgre;

import io.army.criteria.Expression;
import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.TableWrapper;
import io.army.lang.Nullable;

import java.util.List;

public interface PostgreTableWrapper extends TableWrapper {

    @Nullable
    SQLModifier lateral();

    List<Expression<?>> tableSampleFuncList();
}
