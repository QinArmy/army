package io.army.boot;

import io.army.RmSession;

import javax.transaction.xa.XAResource;
import java.sql.Connection;

interface InnerRmSession extends RmSession {

    Connection connection();


    XAResource xaResource();
}
