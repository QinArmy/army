package io.army.example.pill.struct;

import io.army.struct.CodeEnum;

public enum IdentityType implements CodeEnum {

    PERSON(Constant.PERSON),
    ENTERPRISE(Constant.ENTERPRISE);

    private final byte code;

    IdentityType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }


    public interface Constant {
        byte PERSON = 10;
        byte ENTERPRISE = 20;
    }


}
