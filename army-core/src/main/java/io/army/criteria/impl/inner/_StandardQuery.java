package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;

import javax.annotation.Nullable;

public interface _StandardQuery extends _Query {

    @Nullable
    SQLWords lockStrength();


}
