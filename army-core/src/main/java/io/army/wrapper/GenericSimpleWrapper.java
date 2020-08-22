package io.army.wrapper;

import io.army.codec.StatementType;

public interface GenericSimpleWrapper extends SQLWrapper {

    String sql();

    boolean hasVersion();

    StatementType statementType();
}
