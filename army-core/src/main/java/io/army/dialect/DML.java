package io.army.dialect;

import io.army.criteria.Delete;
import io.army.criteria.Insert;
import io.army.criteria.Update;
import io.army.criteria.Visible;
import io.army.wrapper.SQLWrapper;

import java.util.List;

public interface DML extends SQL {

    List<SQLWrapper> insert(Insert insert, Visible visible);


    SQLWrapper update(Update update, Visible visible);


    SQLWrapper delete(Delete delete, Visible visible);

}
