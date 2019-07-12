package com.example.domain.user;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum UserStatus implements CodeEnum {


    NORMAL(0, "正常"),

    FREEZE(900, "冻结"),

    CLOSE(1000, "封号");


    private final int code;

    private final String display;


    private static final Map<Integer, UserStatus> CODE_MAP = CodeEnum.getCodeMap(UserStatus.class);


    public static UserStatus resolve(int code) {
        return CODE_MAP.get(code);
    }

    UserStatus(int code, String display) {
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
