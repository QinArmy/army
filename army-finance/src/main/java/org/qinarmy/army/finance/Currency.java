package org.qinarmy.army.finance;

import org.qinarmy.army.struct.CodeEnum;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * created  on 2019-03-14.
 */
public enum Currency implements CodeEnum {

    NONE(0, "无", ""),

    CNY(156, "人民币", "￥"),
    USD(840, "美元", "$");

    private final int code;

    private final String display;

    private final String symbol;

    private static final Map<Integer, Currency> CODE_MAP = CodeEnum.getCodeMap(Currency.class);


    public static Currency resolve(int code) {
        return CODE_MAP.get(code);
    }

    Currency(int code, @NonNull String display, @NonNull String symbol) {
        this.code = code;
        this.display = display;
        this.symbol = symbol;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    public String symbol() {
        return symbol;
    }
}
