package io.army.criteria.impl;

interface UnionAndQuery<Q> {

    Q leftQuery();

    UnionType unionType();


}
