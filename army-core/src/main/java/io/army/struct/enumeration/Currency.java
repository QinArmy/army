package io.army.struct.enumeration;


import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * @see <a href="https://www.currency-iso.org/dam/downloads/lists/list_one.xml">ISO Currency Codes</a>
 * @since 1.0
 */
public enum Currency implements CodeEnum {

    CNY(156, "人民币", "￥"),
    USD(840, "美元", "$");

    private final int code;

    private final String display;

    private final String symbol;

    private static final Map<Integer, Currency> CODE_MAP = CodeEnum.getCodeMap(Currency.class);


    public static Currency resolve(int code) {
        return CODE_MAP.get(code);
    }

    Currency(int code, String display, String symbol) {
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
