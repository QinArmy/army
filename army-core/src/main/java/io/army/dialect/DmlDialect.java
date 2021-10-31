package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.stmt.Stmt;

import java.util.List;
import java.util.Set;

public interface DmlDialect extends SQL {

    List<Stmt> valueInsert(Insert insert, @Nullable Set<Integer> domainIndexSet, Visible visible);

    Stmt returningInsert(Insert insert, Visible visible);

    Stmt subQueryInsert(Insert insert, Visible visible);

    Stmt update(Update update, Visible visible);

    Stmt returningUpdate(Update update, Visible visible);

    Stmt delete(Delete delete, Visible visible);

    Stmt returningDelete(Delete delete, Visible visible);

}
