package com.example.domain;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/5/19.
 */
public enum ClientType implements CodeEnum {

    WEIBO_H5(0, "微博H5"),
    DESK_BROWSER(100, "桌面浏览器"),
    PHONE_BROWSER(200, "手机浏览器"),

    IOS(300, "苹果手机应用"),
    ANDROID(400, "安卓应用"),
    APPLE(500, "MAC OS 应用"),
    MICRO(600, "微软 OS 应用");


    private final int code;

    private final String display;

    private static final Map<Integer, ClientType> CODE_MAP = CodeEnum.getCodeMap(ClientType.class);


    public static ClientType resolve(int code) {
        return CODE_MAP.get(code);
    }

    ClientType(int code, String display) {
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
