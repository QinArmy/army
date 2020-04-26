package com.example.domain.trade;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/5/20.
 */
public enum TradeProvider implements CodeEnum {

;


    private final int code;

    private final String display;

    private static final Map<Integer, TradeProvider> CODE_MAP = CodeEnum.getCodeMap(TradeProvider.class);


    public static TradeProvider resolve(int code) {
        return CODE_MAP.get(code);
    }

    TradeProvider(int code, String display) {
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
