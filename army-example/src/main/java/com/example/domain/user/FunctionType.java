package com.example.domain.user;

import org.qinarmy.army.struct.CodeEnum;

import java.util.Map;

/**
 * created  on 2018/9/27.
 */
public enum FunctionType implements CodeEnum {

    ASSET_PARTNER(0, "资产合作者"),

    PARTNER_GUARANTOR(100, "合作者的担保人"),

    SYSTEM(8000, "代表系统本身");

    private final int code;

    private final String display;


    private static final Map<Integer, FunctionType> CODE_MAP = CodeEnum.getCodeMap(FunctionType.class);


    public static FunctionType resolve(int code) {
        return CODE_MAP.get(code);
    }

    FunctionType(int code, String display) {
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
