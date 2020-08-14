package com.example.fortune.domain.account;

import com.example.fortune.domain.user.UserType;
import io.army.struct.CodeEnum;
import org.springframework.lang.NonNull;

import java.util.Map;

public enum AccountType implements CodeEnum {

    /**
     * @see UserType#PERSON
     * @see UserType#ENTERPRISE
     */
    LENDER(Constant.LENDER, "account of lender"),

    /**
     * @see UserType#PERSON
     * @see UserType#ENTERPRISE
     */
    BORROWER(Constant.BORROWER, "account of borrower"),

    BALANCE(Constant.BALANCE, "balance account"),

    REPAY(Constant.REPAY, "repay account"),

    /**
     * @see UserType#ENTERPRISE
     */
    ASSET_ENTERPRISE(Constant.ASSET_ENTERPRISE, "account of asset enterprise"),

    /**
     * @see UserType#ENTERPRISE
     */
    GUARANTEE_ENTERPRISE(Constant.GUARANTEE_ENTERPRISE, "account of guarantee enterprise"),

    /**
     * @see UserType#FORTUNE_SELF
     */
    FORTUNE_SELF(Constant.FORTUNE_SELF, "account of depository self");


    private static final Map<Integer, AccountType> CODE_MAP = CodeEnum.getCodeMap(AccountType.class);

    public static AccountType resolve(int code) {
        return CODE_MAP.get(code);
    }

    private final int code;

    private final String display;

    AccountType(int code, String display) {
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


    public interface Constant {
        int LENDER = 100;
        int BORROWER = 200;
        int BALANCE = 300;
        int REPAY = 400;

        int ASSET_ENTERPRISE = 2100;
        int GUARANTEE_ENTERPRISE = 2200;

        int FORTUNE_SELF = 3000;
    }
}
