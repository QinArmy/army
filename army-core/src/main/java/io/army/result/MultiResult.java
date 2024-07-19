package io.army.result;

import io.army.ArmyException;

public interface MultiResult {

    ResultType nextType() throws ArmyException;

}
