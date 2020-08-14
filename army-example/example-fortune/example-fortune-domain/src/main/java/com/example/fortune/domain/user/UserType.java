package com.example.fortune.domain.user;

import com.example.fortune.domain.account.AccountType;
import io.army.struct.CodeEnum;
import org.springframework.lang.NonNull;

import java.util.Map;

public enum UserType implements CodeEnum {

    NONE(0, "NONE"),
    /**
     * @see AccountType#LENDER
     * @see AccountType#BORROWER
     */
    PERSON(Constant.PERSON, "primary user"),

    /**
     * @see AccountType#LENDER
     * @see AccountType#BORROWER
     * @see AccountType#ASSET_ENTERPRISE
     * @see AccountType#GUARANTEE_ENTERPRISE
     */
    ENTERPRISE(Constant.ENTERPRISE, "enterprise user"),

    /**
     * @see AccountType#FORTUNE_SELF
     */
    FORTUNE_SELF(Constant.FORTUNE_SELF, "fortune self");

    private static final Map<Integer, UserType> CODE_MAP = CodeEnum.getCodeMap(UserType.class);

    public static UserType resolve(int code) {
        return CODE_MAP.get(code);
    }

    private final int code;

    private final String display;

    UserType(int code, String display) {
        this.code = code;
        this.display = display;
    }

    public int code() {
        return code;
    }

    @NonNull
    public String display() {
        return this.display;
    }


    interface Constant {
        int PERSON = 100;
        int ENTERPRISE = 200;

        int FORTUNE_SELF = 2000;

    }
}
