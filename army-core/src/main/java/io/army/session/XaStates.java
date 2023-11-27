package io.army.session;

import io.army.util._StringUtils;


/**
 * <p>
 * This enum representing An XA transaction progresses states.
 * <br/>
 * <p>
 * Application developer can get {@link XaStates} by {@link TransactionInfo#valueOf(ArmyOption)} <br/>
 * when {@link RmSession} in XA transaction block. see {@link TransactionInfo#valueOf(ArmyOption)}
 * <p>
 * <br/>
 *
 * @see ArmyOption#XA_STATES
 * @see RmSession
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xa-states.html">XA Transaction States</a>
 * @since 1.0
 */
public enum XaStates {


    /**
     * <p>
     * This instance representing XA transaction started after {@code  RmSession#start(Xid, int, TransactionOption)} method.
     * <br/>
     */
    ACTIVE,

    /**
     * <p>
     * This instance representing XA transaction IDLE after {@code RmSession#end(Xid, int, Function)} method.
     * <br/>
     * <p>
     * This states support one-phase commit.
     * <br/>
     */
    IDLE,

    /**
     * <p>
     * This instance representing XA transaction PREPARED after {@code RmSession#prepare(Xid, Function)} method.
     * <br/>
     */
    PREPARED;


    @Override
    public final String toString() {
        return _StringUtils.enumToString(this);
    }


}
