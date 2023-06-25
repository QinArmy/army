package io.army.criteria;

import io.army.lang.Nullable;
import io.army.util._Exceptions;

public enum Visible {

    ONLY_VISIBLE(Boolean.TRUE),
    ONLY_NON_VISIBLE(Boolean.FALSE),
    BOTH(null);

    public final Boolean value;

    Visible(@Nullable Boolean value) {
        this.value = value;
    }

    public static Visible from(final @Nullable Boolean visible) {
        final Visible visibleEnm;
        if (visible == null) {
            visibleEnm = Visible.BOTH;
        } else if (visible) {
            visibleEnm = Visible.ONLY_VISIBLE;
        } else {
            visibleEnm = Visible.ONLY_NON_VISIBLE;
        }
        return visibleEnm;
    }

    public final boolean isSupport(final Visible visible) {
        final boolean match;
        switch (this) {
            case ONLY_VISIBLE:
                match = visible == ONLY_VISIBLE;
                break;
            case ONLY_NON_VISIBLE:
                match = visible == ONLY_VISIBLE || visible == ONLY_NON_VISIBLE;
                break;
            case BOTH:
                match = true;
                break;
            default:
                throw _Exceptions.unexpectedEnum(this);

        }
        return match;
    }


}
