package io.army.criteria.impl.inner;

import io.army.beans.ReadWrapper;

import java.util.List;

public interface _BatchDML extends _Statement {

    List<? extends ReadWrapper> wrapperList();
}
