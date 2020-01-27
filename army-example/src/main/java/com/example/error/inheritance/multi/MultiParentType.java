package com.example.error.inheritance.multi;

import io.army.struct.CodeEnum;

import javax.annotation.Nonnull;
import java.util.Map;

public enum MultiParentType implements CodeEnum {

    ONE(Constant.ONE, "one"),

    TWO(Constant.TWO, "two"),

    THREE(Constant.THREE, "three");


    private static final Map<Integer, MultiParentType> CODE_MAP = CodeEnum.getCodeMap(MultiParentType.class);


    public static MultiParentType resolve(int code) {
        return CODE_MAP.get(code);
    }

    interface Constant {
        int ONE = 0;
        int TWO = 100;
        int THREE = 200;
    }

    private final int code;

    private final String display;

    MultiParentType(int code, String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @Nonnull
    @Override
    public String display() {
        return display;
    }
}
