package io.army.dialect;

import io.army.wrapper.SimpleSQLWrapper;

public interface UpdateContext extends UpdateDeleteContext {

    @Override
    SimpleSQLWrapper build();
}
