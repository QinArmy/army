package io.army.criteria;

public interface DqlInsert {

    interface _DqlInsertSpec<Q extends DqlInsert> {

        Q asReturningInsert();
    }
}
