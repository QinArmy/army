package io.army.example.pill.struct;

import io.army.struct.CodeEnum;

public enum PillUserType implements CodeEnum {

    NONE(Constant.NONE),
    PERSON(Constant.PERSON),
    ENTERPRISE(Constant.ENTERPRISE),
    PARTNER(Constant.PARTNER),
    SELF(Constant.SELF);

    private final byte code;

    PillUserType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

    public interface Constant {
        byte NONE = 0;
        byte PERSON = 10;
        byte ENTERPRISE = 20;
        byte PARTNER = 30;
        byte SELF = 40;
    }


}
