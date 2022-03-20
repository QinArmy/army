package io.army.bank.domain.account;

import io.army.struct.CodeEnum;

public enum AccountType implements CodeEnum {

    BANK(Constant.BANK),
    PARTNER(Constant.PARTNER),
    GUARANTOR(Constant.GUARANTOR),
    LENDER(Constant.LENDER);

    private final short code;

    AccountType(short code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

    interface Constant {

        short BANK = 0;
        short PARTNER = 100;
        short GUARANTOR = 200;
        short LENDER = 300;
        short BORROWER = 400;

    }


}
