package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.wrapper.BatchSQLWrapper;
import io.army.wrapper.SQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.List;

public interface DML extends SQL {


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
    SQLWrapper update(Update update, Visible visible);

    /**
     * @return a unmodifiable list
     */
    List<SimpleSQLWrapper> delete(Delete delete, Visible visible);


}
