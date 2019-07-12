package com.example.domain.user;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 认证状态,即实名状态
 * created  on 2018/9/19.
 */
public enum AuthStatus implements CodeEnum {

    NONE(0, "未实名"),

    AUTH(100, "已认证(实名)"),

    FACE_AUTH(300, "人脸识别");


    private final int code;

    private final String display;


    private static final Map<Integer, AuthStatus> CODE_MAP = CodeEnum.getCodeMap(AuthStatus.class);


    public static AuthStatus resolve(int code) {
        return CODE_MAP.get(code);
    }

    AuthStatus(int code, String display) {
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
