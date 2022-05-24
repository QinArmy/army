package io.army.dialect;

import io.army.stmt.SimpleStmt;

public interface _SelectContext extends PrimaryQueryContext {


    @Override
    SimpleStmt build();


}
