package io.army.example.bank.service;

import io.army.example.bank.BankException;
import io.army.example.bank.bean.BankCode;

public class InvalidRequestNoException extends BankException {

    public final String requestNo;

    public InvalidRequestNoException(String message, BankCode bankCode, String requestNo) {
        super(message, bankCode);
        this.requestNo = requestNo;
    }

    public String getRequestNo() {
        return requestNo;
    }

}
