package com.example.domain.user;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 风险类型
 * created  on 2018/9/27.
 */
public enum RiskType implements CodeEnum {


    NONE(0, "无"),
    CONSERVATIVE(100, "保守"),
    ROBUST(200, "稳健"),
    POSITIVE(300, "积极");

    private final int code;

    private final String display;


    private static final Map<Integer, RiskType> CODE_MAP = CodeEnum.getCodeMap(RiskType.class);


    public static RiskType resolve(int code) {
        return CODE_MAP.get(code);
    }

    RiskType(int code, String display) {
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
