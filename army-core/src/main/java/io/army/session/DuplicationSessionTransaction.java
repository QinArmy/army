package io.army.session;

public class DuplicationSessionTransaction extends SessionException {

    public DuplicationSessionTransaction(String message) {
        super(message);
    }

}
