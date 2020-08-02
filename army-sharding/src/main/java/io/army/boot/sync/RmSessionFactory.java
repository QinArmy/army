package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.sync.GenericSyncSessionFactory;
import io.army.tx.TransactionOption;

interface RmSessionFactory extends GenericSyncSessionFactory, GenericRmSessionFactory {

    int databaseIndex();

    RmSession build(TransactionOption option);


}
