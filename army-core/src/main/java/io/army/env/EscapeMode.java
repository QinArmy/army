package io.army.env;

import io.army.criteria.Support;
import io.army.meta.BooleanMode;
import io.army.util._StringUtils;

import static io.army.dialect.Database.PostgreSQL;

public enum EscapeMode {

    DEFAULT(BooleanMode.TRUE),

    BACK_SLASH(BooleanMode.TRUE),

    UNICODE(BooleanMode.TRUE),

    DOLLAR_QUOTED(BooleanMode.TRUE),

    DEFAULT_NO_TYPE(BooleanMode.FALSE),

    BACK_SLASH_NO_TYPE(BooleanMode.FALSE),

    UNICODE_NO_TYPE(BooleanMode.FALSE),

    DOLLAR_QUOTED_NO_TYPE(BooleanMode.FALSE),

    @Support(PostgreSQL)
    ARRAY_ELEMENT(BooleanMode.UNKNOWN),
    @Support(PostgreSQL)
    ARRAY_ELEMENT_PART(BooleanMode.UNKNOWN);

    public final BooleanMode typeMode;

    EscapeMode(BooleanMode typeMode) {
        this.typeMode = typeMode;
    }


    public final EscapeMode switchNoTypeMode() {
        final EscapeMode mode;
        switch (this) {
            case DEFAULT:
                mode = EscapeMode.DEFAULT_NO_TYPE;
                break;
            case BACK_SLASH:
                mode = EscapeMode.BACK_SLASH_NO_TYPE;
                break;
            case UNICODE:
                mode = EscapeMode.UNICODE_NO_TYPE;
                break;
            case DOLLAR_QUOTED:
                mode = EscapeMode.DOLLAR_QUOTED_NO_TYPE;
                break;
            default:
                mode = this;
        }
        return mode;
    }

    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
