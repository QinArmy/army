package io.army.criteria;

public interface DqlInsert extends Item {

    interface _DqlInsertSpec<Q extends Item> {

        Q asReturningInsert();
    }
}
