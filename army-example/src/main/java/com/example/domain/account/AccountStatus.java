package com.example.domain.account;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum AccountStatus implements CodeEnum {


    NORMAL(0, "正常"),

    FREEZE(900, "冻结"),

    CLOSE(1000, "封账户");


    private final int code;

    private final String display;


    private static final Map<Integer, AccountStatus> CODE_MAP = CodeEnum.getCodeMap(AccountStatus.class);


    public static AccountStatus resolve(int code) {
        return CODE_MAP.get(code);
    }

    AccountStatus(int code, String display) {
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
