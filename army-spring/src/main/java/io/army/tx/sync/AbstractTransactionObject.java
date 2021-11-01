package io.army.tx.sync;


import io.army.DataAccessException_0;
import io.army.sync.GenericSyncApiSession;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.util.Assert;

abstract class AbstractTransactionObject<S extends GenericSyncApiSession>
        implements SmartTransactionObject {

    S session;

    AbstractTransactionObject() {
    }

    /*################################## blow SmartTransactionObject method ##################################*/


    @Override
    public final void flush() {
        try {
            this.session.flush();
        } catch (DataAccessException_0 e) {
            throw SpringUtils.convertArmyAccessException(e);
        }
    }



    /*################################## blow custom method ##################################*/

    final S suspend() throws IllegalStateException {
        S currentSession = this.session;
        Assert.state(currentSession != null, "session is null,ArmyTransactionObject state error.");
        this.session = null;
        return currentSession;
    }

    final void setSession(S newSession) throws IllegalStateException {
        Assert.state(this.session == null, "session not null,ArmyTransactionObject state error.");
        this.session = newSession;
    }
}
