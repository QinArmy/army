package io.army.criteria;

import io.army.lang.Nullable;

public enum Visible {

    ONLY_VISIBLE(Boolean.TRUE),
    ONLY_NON_VISIBLE(Boolean.FALSE),
    BOTH(null);

    private final Boolean visible;

    Visible(@Nullable Boolean visible) {
        this.visible = visible;
    }

    public static Visible resolve(@Nullable Boolean visible) {
        Visible visibleEnm;
        if (visible == null) {
            visibleEnm = Visible.BOTH;
        } else if (Boolean.TRUE.equals(visible)) {
            visibleEnm = Visible.ONLY_VISIBLE;
        } else {
            visibleEnm = Visible.ONLY_NON_VISIBLE;
        }
        return visibleEnm;
    }

    @Nullable
    public Boolean value() {
        return visible;
    }
}
