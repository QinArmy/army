package io.army.criteria.impl;

import io.army.criteria.GroupAble;
import io.army.criteria.GroupElement;
import io.army.criteria.HavingAble;
import io.army.util.ArrayUtils;
import org.springframework.lang.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * created  on 2019-01-31.
 */
abstract class AbstractGroupAble extends AbstractHavingAble implements GroupAble {

    private List<GroupElement<?>> groupElementList = Collections.emptyList();


    @NonNull
    @Override
    public final HavingAble group(GroupElement<?>... groupElements) {
        groupElementList = ArrayUtils.asUnmodifiableList(groupElements);
        return this;
    }
}
