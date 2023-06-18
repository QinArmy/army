package io.army.annotation;

public enum UpdateMode {

    UPDATABLE,
    IMMUTABLE,

    @Deprecated
    ONLY_NULL,

    @Deprecated
    ONLY_DEFAULT


}
