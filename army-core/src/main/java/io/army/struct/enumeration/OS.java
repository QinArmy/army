package io.army.struct.enumeration;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * @since 1.0
 */
public enum OS implements CodeEnum {

    LINUX(0, "linux"),

    MAC(100, "mac"),

    WINDOWS(200, "windows"),

    SOLARIS(300, "solaris");

    private static final Map<Integer, OS> CODE_MAP = CodeEnum.getInstanceMap(OS.class);

    public static OS resolve(int code) {
        return CODE_MAP.get(code);
    }


    private final int code;

    private final String display;

    OS(int code, String display) {
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
