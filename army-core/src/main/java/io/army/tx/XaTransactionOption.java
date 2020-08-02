package io.army.tx;

public interface XaTransactionOption extends TransactionOption {

    byte[] globalTransactionId();


}
