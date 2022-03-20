package io.army.bank.domain.user;

import io.army.struct.CodeEnum;

public enum BankUserType implements CodeEnum {

    BANK(Constant.BANK),
    PERSON(Constant.PERSON),
    INVEST_PARTNER(Constant.INVEST_PARTNER),
    BORROW_PARTNER(Constant.BORROW_PARTNER);

    private final byte code;

    BankUserType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }


    interface Constant {

        byte BANK = 0;

        byte PERSON = 10;

        byte INVEST_PARTNER = 20;

        byte BORROW_PARTNER = 30;

    }

}
