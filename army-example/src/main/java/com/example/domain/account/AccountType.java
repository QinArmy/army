package com.example.domain.account;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum AccountType implements CodeEnum {

    NONE(Account.NONE_VALUE, "无"),


    BALANCE(Account.BALANCE_VALUE, "余额账户"),

    INVEST(Account.INVEST_VALUE, "投资账户"),

    LENDER(Account.LENDER_VALUE, "出借人账户"),

    BORROWER(Account.BORROWER_VALUE, "借款账户"),

    FUNCTION(Account.FUNCTION_VALUE, "功能账户");


    private final int code;

    private final String display;


    private static final Map<Integer, AccountType> CODE_MAP = CodeEnum.getCodeMap(AccountType.class);


    public static AccountType resolve(int code) {
        return CODE_MAP.get(code);
    }

    AccountType(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

}
