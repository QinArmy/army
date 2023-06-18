package io.army.dialect;


import io.army.stmt._StmtParams;


/**
 * package interface
 */
interface BatchSpecContext extends _SqlContext, _StmtParams {

    int nextGroup() throws ArrayIndexOutOfBoundsException;

    int groupSize();
}
