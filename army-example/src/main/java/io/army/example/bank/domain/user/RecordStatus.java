package io.army.example.bank.domain.user;

import io.army.struct.CodeEnum;

public enum RecordStatus implements CodeEnum {

    CREATED((byte) 0),
    HANDLING((byte) 10),
    FAILURE((byte) 20),
    SUCCESS((byte) 30);


    private final byte code;

    RecordStatus(byte code) {
        this.code = code;
    }

    @Override
    public int code() {
        return this.code;
    }


}
