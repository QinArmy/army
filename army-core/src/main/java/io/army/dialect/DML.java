package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.domain.IDomain;

import java.util.List;

public interface DML extends SQL {

    List<SQLWrapper> insert(IDomain domain);

    /**
     * @return a unmodifiable list
     */
    List<SQLWrapper> insert(Insert insert, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<BatchSQLWrapper> batchInsert(Insert insert, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<SQLWrapper> update(Update update, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<SQLWrapper> delete(Delete delete, Visible visible);


}
