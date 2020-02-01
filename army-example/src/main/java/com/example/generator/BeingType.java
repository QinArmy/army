package com.example.generator;

import io.army.lang.NonNull;
import io.army.struct.CodeEnum;

import java.util.Map;

public enum BeingType implements CodeEnum {

    BEING(Constant.BEING, "生物"),
    ANIMAL(Constant.ANIMAL, "动物"),
    BOTANY(Constant.BOTANY, "植物");


    private static final Map<Integer, BeingType> CODE_MAP = CodeEnum.getCodeMap(BeingType.class);


    public static BeingType resolve(int code) {
        return CODE_MAP.get(code);
    }

    interface Constant {
        int BEING = 0;
        int ANIMAL = 100;
        int BOTANY = 200;
    }


    private final int code;

    private final String display;

    BeingType(int code, String display) {
        this.code = code;
        this.display = display;
    }


    @Override
    public int code() {
        return code;
    }

    @NonNull
    @Override
    public String display() {
        return display;
    }
}
