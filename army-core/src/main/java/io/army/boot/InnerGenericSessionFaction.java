package io.army.boot;

import io.army.GenericSessionFactory;
import io.army.dialect.Dialect;

public interface InnerGenericSessionFaction extends GenericSessionFactory {

    Dialect dialect();

}
