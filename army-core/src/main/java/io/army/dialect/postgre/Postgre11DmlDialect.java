package io.army.dialect.postgre;

import io.army.dialect._AbstractDialect;

class Postgre11DmlDialect extends _AbstractDialect {

    Postgre11DmlDialect(Postgre11Dialect dialect) {
        super(dialect);
    }

    @Override
    public boolean supportOnlyDefault() {
        return false;
    }



}
