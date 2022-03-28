package io.army.session;


import io.army.ArmyException;

public final class OptimisticLockException extends ArmyException {


    public OptimisticLockException(String message) {
        super(message);
    }

}
