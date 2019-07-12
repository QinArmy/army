package com.example.domain.user;

import io.army.struct.CodeEnum;

import java.util.Map;

import static com.example.domain.user.User.*;

/**
 * created  on 2018/9/19.
 */
public enum UserType implements CodeEnum {

    NONE(NONE_VALUE, "无"),

    PERSON(PERSON_VALUE, "个人"),

    ENTERPRISE(ENTERPRISE_VALUE, "企业"),


    FUNCTION(FUNCTION_VALUE, "功能");


    private final int code;

    private final String display;


    private static final Map<Integer, UserType> CODE_MAP = CodeEnum.getCodeMap(UserType.class);


    public static UserType resolve(int code) {
        return CODE_MAP.get(code);
    }

    UserType(int code, String display) {
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
