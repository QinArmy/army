package com.example.domain.trade;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/5/20.
 */
public enum TradeProvider implements CodeEnum {

    YEXEPAY(0, "支付"),
    FUXIOU(100, "支付"),
    ALXLINPAY(200, "支付"),
    UCXFPAY(300, "支付"),
    BAXOFOO(400, "付"),
    REXAPAL(500, "支付"),
    BIXLL99(600, "支付"),
    LIAXNLIAN(700, "支付"),
    SUMXAPAY(8, "付"),
    NEXWPAY(900, "支付"),
    BFXBPAY(1000, "宝"),
    UMXPAY(1100, "优势"),
    XXWXBANK(1200, "银行");


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
