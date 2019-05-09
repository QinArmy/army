package com.example.domain.yesterday;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum YesterdayRecordType implements CodeEnum {

    NONE(0, "无"),

    INTELLIGENCE_YIELD(100, "智投收益"),

    OPTIMIZE_YIELD(200, "优选收益"),


    INTELLIGENCE_DATE(10000, "智投债务"),

    OPTIMIZE_DATE(10100, "优选债务"),
    ;

    private final int code;

    private final String display;


    private static final Map<Integer, YesterdayRecordType> CODE_MAP = CodeEnum.getCodeMap(YesterdayRecordType.class);


    public static YesterdayRecordType resolve(int code) {
        return CODE_MAP.get(code);
    }

    YesterdayRecordType(int code, String display) {
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
