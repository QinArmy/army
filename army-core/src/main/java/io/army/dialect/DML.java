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

    SQLWrapper returningInsert(Insert insert, Visible visible);

    SQLWrapper subQueryInsert(Insert insert, Visible visible);

    SQLWrapper update(Update update, Visible visible);

    SQLWrapper returningUpdate(Update update, Visible visible);

    SQLWrapper delete(Delete delete, Visible visible);

    SQLWrapper returningDelete(Delete delete, Visible visible);

}
