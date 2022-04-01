package io.army.example.bank.domain.account;

import io.army.struct.CodeEnum;

public enum BankAccountType implements CodeEnum {

    BANK(Constant.BANK),
    LENDER(Constant.LENDER),
    BORROWER(Constant.BORROWER),
    PARTNER(Constant.PARTNER),
    LENDER_BUSINESS(Constant.LENDER_BUSINESS),
    BORROWER_BUSINESS(Constant.BORROWER_BUSINESS),
    GUARANTOR(Constant.GUARANTOR);


    private final short code;

    BankAccountType(short code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

    interface Constant {

        short BANK = 0;
        short LENDER = 100;
        short BORROWER = 200;
        short PARTNER = 300;
        short LENDER_BUSINESS = 400;
        short BORROWER_BUSINESS = 500;
        short GUARANTOR = 600;

    }


}
