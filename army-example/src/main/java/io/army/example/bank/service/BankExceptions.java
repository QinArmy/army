package io.army.example.bank.service;

import io.army.example.bank.BankException;
import io.army.example.bank.ban.BankCode;

public abstract class BankExceptions {

    protected BankExceptions() {
        throw new UnsupportedOperationException();
    }


    public static BankException partnerNotExists(String partnerNo) {
        String m = String.format("Partner[%s] not exists.", partnerNo);
        return new BankException(m, BankCode.PARTNER_NOT_EXISTS);
    }

    public static BankException duplicationUser(String partnerNo, String userNo) {
        String m = String.format("Partner[%s] user[%s] exists", partnerNo, userNo);
        return new BankException(m, BankCode.ACCOUNT_DUPLICATION);
    }

    public static BankException invalidRequestNo(String requestNo) {
        String m = String.format("RequestNo[%s] is invalid", requestNo);
        return new BankException(m, BankCode.REQUEST_NO_INVALID);
    }

}
