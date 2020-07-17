package io.army.boot;

import io.army.GenericSyncSessionFactory;
import io.army.context.spi.CurrentSessionContext;

interface InnerGenericSyncSessionFactory extends InnerGenericSessionFaction, GenericSyncSessionFactory {

    CurrentSessionContext currentSessionContext();

    DomainValuesGenerator domainValuesGenerator();
}
