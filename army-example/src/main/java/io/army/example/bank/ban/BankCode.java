package io.army.example.bank.ban;

public enum BankCode {

    PARTNER_NOT_EXISTS(1001, "partner not exits"),

    ACCOUNT_DUPLICATION(2001, "partner not exits");


    public final int code;

    public final String message;

    BankCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
