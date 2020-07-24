package io.army.boot.sync;

import io.army.boot.DomainValuesGenerator;
import io.army.boot.InnerGenericSessionFaction;
import io.army.context.spi.CurrentSessionContext;
import io.army.sync.GenericSyncSessionFactory;

interface InnerGenericSyncSessionFactory extends InnerGenericSessionFaction, GenericSyncSessionFactory {

    CurrentSessionContext currentSessionContext();

    DomainValuesGenerator domainValuesGenerator();
}
