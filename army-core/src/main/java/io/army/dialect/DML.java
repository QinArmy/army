package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.wrapper.SQLWrapper;

import java.util.List;
import java.util.Set;

public interface DML extends SQL {

    List<SQLWrapper> valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible);

    SQLWrapper subQueryInsert(Insert insert, Visible visible);

    SQLWrapper simpleUpdate(Update update, Visible visible);

    List<SQLWrapper> batchUpdate(Update update, @Nullable Set<Integer> namedParamIexSet, Visible visible);

    SQLWrapper simpleDelete(Delete delete, Visible visible);

    List<SQLWrapper> batchDelete(Delete delete, @Nullable Set<Integer> namedParamIexSet, Visible visible);

}
