package io.army.boot;

import io.army.context.spi.CurrentSessionContext;
import io.army.sync.GenericSyncSessionFactory;

interface InnerGenericSyncSessionFactory extends InnerGenericSessionFaction, GenericSyncSessionFactory {

    CurrentSessionContext currentSessionContext();

    DomainValuesGenerator domainValuesGenerator();
}
