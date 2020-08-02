package io.army.boot.sync;

import javax.sql.XAConnection;

interface InnerRmSession extends RmSession, InnerGenericRmSession {

    XAConnection connection();
}
