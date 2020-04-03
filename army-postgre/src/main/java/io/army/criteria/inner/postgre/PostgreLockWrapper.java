package io.army.criteria.inner.postgre;

import io.army.criteria.SQLModifier;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;

public interface PostgreLockWrapper {

    SQLModifier lockMode();

    List<TableMeta<?>> lockTableList();

    @Nullable
    SQLModifier lockOption();
}
