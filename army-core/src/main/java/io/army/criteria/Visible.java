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

    @Nullable
    public Boolean getValue() {
        return visible;
    }
}
