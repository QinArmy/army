package io.army.sync;

import io.army.sync.GenericSyncApiSessionFactory;

interface InnerGenericSyncApiSessionFactory extends GenericSyncApiSessionFactory {

    boolean springApplication();
}
