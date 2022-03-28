package io.army.example.bank.ban;

public enum BankCode {

    REQUEST_NO_INVALID(440, "requestNo is invalid"),

    PARTNER_NOT_EXISTS(1001, "partner not exits"),

    ACCOUNT_DUPLICATION(2001, "account not exits");


    public final int code;

    public final String message;

    BankCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
