package io.army.example.struct;

import io.army.struct.CodeEnum;

public enum UserType implements CodeEnum {

    PERSON(Constant.PERSON),
    ENTERPRISE(Constant.ENTERPRISE),
    PARTNER(Constant.PARTNER),
    SELF(Constant.SELF);

    private final byte code;

    UserType(byte code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

   public interface Constant {
       byte PERSON = 10;
       byte ENTERPRISE = 20;
       byte PARTNER = 30;
       byte SELF = 40;
   }


}
