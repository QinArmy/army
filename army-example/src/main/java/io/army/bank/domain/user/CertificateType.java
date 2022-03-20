package io.army.bank.domain.user;

import io.army.struct.CodeEnum;

public enum CertificateType implements CodeEnum {

    NONE(Constant.NONE),
    PERSON(Constant.PERSON),
    ENTERPRISE(Constant.ENTERPRISE);


    private final byte code;

    CertificateType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }


    interface Constant {

        byte NONE = 0;
        byte PERSON = 10;
        byte ENTERPRISE = 20;
    }


}
