package io.army.boot.sync;

import io.army.SessionException;
import io.army.session.DialectSessionFactory;
import io.army.sync.GenericSyncSessionFactory;
import io.army.tx.XaTransactionOption;

interface RmSessionFactory extends GenericSyncSessionFactory, DialectSessionFactory {

    void initialize();

    RmSession build(XaTransactionOption option) throws SessionException;


}
