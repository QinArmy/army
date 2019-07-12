package com.example.domain.trade;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum TradeSubStatus implements CodeEnum {


    /**
     * 这是默认值,此时状态应该显示
     */
    NONE(0, ""),


    ONE(100, "状态1"),

    TWO(200, "状态2"),

    THREE(300, "状态3"),

    FOUR(400, "状态4");


    private final int code;

    private final String display;


    private static final Map<Integer, TradeSubStatus> CODE_MAP = CodeEnum.getCodeMap(TradeSubStatus.class);


    public static TradeSubStatus resolve(int code) {
        return CODE_MAP.get(code);
    }


    TradeSubStatus(int code, String display) {
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
