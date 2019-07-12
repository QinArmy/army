package io.army.struct.enumeration;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 性别
 * created  on 2018/9/27.
 */
public enum Gender implements CodeEnum {

    N(0, "未知"),

    M(100, "男"),

    F(200, "女");


    private final int code;

    private final String display;


    private static final Map<Integer, Gender> CODE_MAP = CodeEnum.getCodeMap(Gender.class);


    public static Gender resolve(int code) {
        return CODE_MAP.get(code);
    }

    Gender(int code, String display) {
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
