package io.army.boot;

import io.army.TmSessionFactory;
import io.army.boot.sync.InnerGenericSyncSessionFactory;

interface InnerTmSessionFactory extends TmSessionFactory, InnerGenericSyncSessionFactory {


}
