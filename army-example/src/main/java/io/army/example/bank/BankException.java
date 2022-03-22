package io.army.example.bank;

import io.army.example.bank.ban.BankCode;

public class BankException extends RuntimeException {


    public final BankCode bankCode;

    public BankException(String message, BankCode bankCode) {
        super(message);
        this.bankCode = bankCode;
    }

    public BankException(String message, Throwable cause, BankCode bankCode) {
        super(message, cause);
        this.bankCode = bankCode;
    }


}
