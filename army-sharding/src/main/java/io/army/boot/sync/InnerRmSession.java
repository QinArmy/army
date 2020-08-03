package io.army.boot.sync;

import javax.transaction.xa.XAResource;
import java.sql.SQLException;

interface InnerRmSession extends RmSession, InnerGenericRmSession {

    XAResource xaResource() throws SQLException;
}
