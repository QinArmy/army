package io.army.session;


public final class NonUniqueException extends DataAccessException {

    public NonUniqueException(String message) {
        super(message);
    }

}
