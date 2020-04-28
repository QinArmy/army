package io.army.tx;

import io.army.Session;

public interface Transaction extends GenericTransaction {

    Session session();


}
