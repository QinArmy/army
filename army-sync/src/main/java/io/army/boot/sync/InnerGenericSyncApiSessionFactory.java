package io.army.boot.sync;

import io.army.sync.GenericSyncApiSessionFactory;

interface InnerGenericSyncApiSessionFactory extends GenericSyncApiSessionFactory {

    boolean springApplication();
}
