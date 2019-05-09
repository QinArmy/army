package com.example.domain.trade;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/5/20.
 */
public enum TradeProvider implements CodeEnum {

    YEEPAY(0, "易宝支付"),
    FUIOU(100, "富友支付"),
    ALLINPAY(200, "通联支付"),
    UCFPAY(300, "先锋支付"),
    BAOFOO(400, "宝付"),
    REAPAL(500, "融宝支付"),
    BILL99(600, "快钱支付"),
    LIANLIAN(700, "连连支付"),
    SUMAPAY(8, "丰付"),
    NEWPAY(900, "新生支付"),
    BFBPAY(1000, "邦付宝"),
    UMPAY(1100, "联动优势"),
    XWBANK(1200, "新网银行");


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
