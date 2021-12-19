package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.stmt.Stmt;

public interface DmlDialect extends SqlDialect {


    Stmt valueInsert(Insert insert, Visible visible);

    Stmt returningInsert(Insert insert, Visible visible);

    Stmt subQueryInsert(Insert insert, Visible visible);

    Stmt update(Update update, Visible visible);

    Stmt returningUpdate(Update update, Visible visible);

    Stmt delete(Delete delete, Visible visible);

    Stmt returningDelete(Delete delete, Visible visible);


}
