package io.army.env;

import io.army.criteria.Support;

import static io.army.dialect.Database.PostgreSQL;

public enum EscapeMode {

    DEFAULT,
    BACK_SLASH,

    @Support(PostgreSQL)
    ARRAY_ELEMENT,
    @Support(PostgreSQL)
    ARRAY_ELEMENT_PART

}
