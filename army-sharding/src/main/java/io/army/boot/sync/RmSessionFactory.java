package io.army.boot.sync;

import io.army.GenericRmSessionFactory;
import io.army.SessionException;
import io.army.sync.GenericSyncSessionFactory;
import io.army.tx.XaTransactionOption;

interface RmSessionFactory extends GenericSyncSessionFactory, GenericRmSessionFactory {

    void initialize();

    RmSession build(XaTransactionOption option) throws SessionException;


}
