package io.army.dialect;

import io.army.criteria.DataField;

public interface _SetClauseContext extends _SqlContext {


    void appendSetLeftItem(DataField dataField);

}
