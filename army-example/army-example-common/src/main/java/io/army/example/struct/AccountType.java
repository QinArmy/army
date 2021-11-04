package io.army.example.struct;

import io.army.struct.CodeEnum;

public enum AccountType implements CodeEnum {

    LENDER(Constant.LENDER),
    BORROWER(Constant.BORROWER),
    GUARANTOR(Constant.GUARANTOR),
    ;

    private final byte code;

    AccountType(byte code) {
        this.code = code;
    }

    @Override
    public int code() {
        return 0;
    }


    interface Constant {
        byte LENDER = 10;
        byte BORROWER = 20;
        byte GUARANTOR = 30;

    }


}
