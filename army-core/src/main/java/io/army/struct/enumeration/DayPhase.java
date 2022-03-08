package io.army.struct.enumeration;


import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 节日的阶段
 *
 * @since 1.0
 */
public enum DayPhase implements CodeEnum {

    /**
     * 如: 春节,大年初一
     */
    OFFICIAL(0, "正式节日"),

    /**
     * 虽然也是节日的一部分但不是正式的一天,如: 元旦的前一天
     */
    BEFORE(100, "节日前"),

    /**
     * 虽然也是节日的一部分但不是正式的一天,如:春节,大年初二
     */
    AFTER(200, "节日后");


    private static final Map<Integer, DayPhase> CODE_MAP = CodeEnum.getInstanceMap(DayPhase.class);


    public static DayPhase resolve(int code) {
        return CODE_MAP.get(code);
    }

    private final int code;

    private final String display;


    DayPhase(int code, String display) {
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
