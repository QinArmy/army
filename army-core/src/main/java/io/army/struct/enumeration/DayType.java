package io.army.struct.enumeration;


import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 特别日 的类型
 *
 * @since 1.0
 */
public enum DayType implements CodeEnum {

    GENERAL(0, "普通日"),
    HOLIDAY(100, "法定假日"),
    WORKDAY(200, "因法定假日而工作的周末"),
    MEMORIAL_DAY(300, "纪念日");


    private static final Map<Integer, DayType> CODE_MAP = CodeEnum.getCodeMap(DayType.class);


    public static DayType resolve(int code) {
        return CODE_MAP.get(code);
    }

    private final int code;

    private final String display;


    DayType(int code, String display) {
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
