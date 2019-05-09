package com.example.domain.trade;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum TradeStatus implements CodeEnum {

    INIT(0, "创建"),

    HANDLING(100, "处理中"),

    FAILURE(200, "失败"),

    CLOSE(300, "关闭"),

    SUCCESS(900, "成功");


    private final int code;

    private final String display;


    private static final Map<Integer, TradeStatus> CODE_MAP = CodeEnum.getCodeMap(TradeStatus.class);


    public static TradeStatus resolve(int code) {
        return CODE_MAP.get(code);
    }

    TradeStatus(int code, String display) {
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
