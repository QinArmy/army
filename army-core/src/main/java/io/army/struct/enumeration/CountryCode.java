package io.army.struct.enumeration;


import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 国家代码
 *
 * @see <a href="https://www.iso.org/obp/ui/#search/code/">ISO Country Codes</a>
 */
public enum CountryCode implements CodeEnum {

    CN(0, "中国", "CHINA");


    private static final Map<Integer, CountryCode> CODE_MAP = CodeEnum.getCodeMap(CountryCode.class);


    public static CountryCode resolve(int code) {
        return CODE_MAP.get(code);
    }


    private final int code;

    private final String display;

    private final String fullName;

    CountryCode(int code, String display, String fullName) {
        this.code = code;
        this.display = display;
        this.fullName = fullName;
    }


    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    public String fullName() {
        return fullName;
    }
}
