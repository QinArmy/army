package io.army.example.bank.domain.user;

import io.army.struct.CodeEnum;

public enum RegionType implements CodeEnum {

    NONE(Constant.NONE),
    PROVINCE(Constant.PROVINCE),
    CITY(Constant.CITY);

    private final byte codeValue;

    RegionType(final int codeValue) {
        assert codeValue >= Byte.MIN_VALUE && codeValue <= Byte.MAX_VALUE;
        this.codeValue = (byte) codeValue;
    }

    @Override
    public final int code() {
        return this.codeValue;
    }


    interface Constant {

        byte NONE = 0;
        byte PROVINCE = 10;
        byte CITY = 20;

    }


}
