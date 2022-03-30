package io.army.example.bank.domain.user;

import io.army.struct.CodeEnum;

public enum BankUserType implements CodeEnum {

    BANK(Constant.BANK),
    PERSON(Constant.PERSON),
    PARTNER(Constant.PARTNER);

    private final byte code;

    BankUserType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

    public final boolean isPartner() {
        return this == PARTNER;
    }


    interface Constant {

        byte BANK = 0;

        byte PERSON = 10;

        byte PARTNER = 20;

    }

}
