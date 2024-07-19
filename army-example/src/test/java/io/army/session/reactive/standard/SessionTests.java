package io.army.session.reactive.standard;


import io.army.session.ReactiveLocalSession;
import io.army.session.ReactiveSession;
import io.army.transaction.Isolation;
import io.army.transaction.TransactionInfo;
import io.army.transaction.TransactionOption;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(dataProvider = "localSessionProvider")
public class SessionTests extends SessionSupport {


    /**
     * @see ReactiveSession#transactionInfo()
     */
    @Test
    public void transactionInfo(final ReactiveLocalSession session) {
        final TransactionInfo info;
        info = session.transactionInfo()
                .block();

        Assert.assertNotNull(info);

        LOG.debug("info: {}", info);
    }

    /**
     * @see ReactiveSession#setTransactionCharacteristics(TransactionOption)
     * @see ReactiveSession#sessionTransactionCharacteristics()
     */
    @Test
    public void setTransactionCharacteristics(final ReactiveLocalSession session) {
        final TransactionInfo sessionTxInfo;
        sessionTxInfo = session.sessionTransactionCharacteristics()
                .block();

        Assert.assertNotNull(sessionTxInfo);

        TransactionOption option;
        option = TransactionOption.option(Isolation.READ_COMMITTED, true);

        session.setTransactionCharacteristics(option)
                .then(session.sessionTransactionCharacteristics())
                .doOnNext(info -> {
                    Assert.assertFalse(info.inTransaction());
                    Assert.assertEquals(info.isolation(), Isolation.READ_COMMITTED);
                    Assert.assertTrue(info.isReadOnly());
                })
                .block();


        option = TransactionOption.option(sessionTxInfo.isolation());

        session.setTransactionCharacteristics(option)
                .then(session.sessionTransactionCharacteristics())
                .doOnNext(info -> {
                    Assert.assertFalse(info.inTransaction());
                    Assert.assertEquals(info.isolation(), sessionTxInfo.isolation());
                    Assert.assertFalse(info.isReadOnly());
                })
                .block();
    }


}
