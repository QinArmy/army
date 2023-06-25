package io.army.dialect;


import io.army.stmt.StmtParams;


/**
 * package interface
 */
interface BatchSpecContext extends _SqlContext, StmtParams {

    int nextGroup() throws ArrayIndexOutOfBoundsException;

    int groupSize();
}
