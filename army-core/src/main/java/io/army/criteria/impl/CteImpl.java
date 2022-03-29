package io.army.criteria.impl;

import io.army.criteria.Cte;
import io.army.criteria.SubQuery;
import io.army.util._CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

final class CteImpl implements Cte {

    static CteImpl create(String name, List<String> columnList, SubQuery subQuery) {
        Objects.requireNonNull(subQuery);
        return new CteImpl(name, columnList, subQuery);
    }

    static CteImpl create(String name, SubQuery subQuery) {
        Objects.requireNonNull(subQuery);
        return new CteImpl(name, Collections.emptyList(), subQuery);
    }

    private final String name;

    private final List<String> columnList;

    private final SubQuery subQuery;

    private CteImpl(String name, List<String> columnList, SubQuery subQuery) {
        this.name = name;
        this.columnList = _CollectionUtils.asUnmodifiableList(columnList);
        this.subQuery = subQuery;
    }

    private CteImpl(String name, SubQuery subQuery) {
        this.name = name;
        this.columnList = Collections.emptyList();
        this.subQuery = subQuery;
    }


}
