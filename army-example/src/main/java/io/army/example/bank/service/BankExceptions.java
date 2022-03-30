package io.army.example.bank.service;

import io.army.example.bank.BankException;
import io.army.example.bank.bean.BankCode;
import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.BankUserType;

import java.time.LocalDateTime;

public abstract class BankExceptions {

    protected BankExceptions() {
        throw new UnsupportedOperationException();
    }


    public static BankException partnerNotExists(String partnerNo) {
        String m = String.format("Partner[%s] not exists.", partnerNo);
        return new BankException(m, BankCode.PARTNER_NOT_EXISTS);
    }

    public static BankException duplicationAccount(String partnerNo, String userNo, BankAccountType accountType) {
        String m = String.format("Partner[%s] user[%s] %s[%s]exists"
                , partnerNo, userNo, BankAccountType.class.getName(), accountType);
        return new BankException(m, BankCode.ACCOUNT_DUPLICATION);
    }

    public static BankException duplicationUser(BankUserType userType) {
        String m = String.format("user type[%s] duplication", userType);
        return new BankException(m, BankCode.USER_DUPLICATION);
    }

    public static BankException regionNotExists(String requestNo, String region) {
        String m = String.format("RequestNo[%s] region[%s] not exists.", requestNo, region);
        return new BankException(m, BankCode.REGION_NOT_EXISTS);
    }

    public static BankException registerRecordFailed(String requestNo) {
        String m = String.format("Register requestNo[%s] have failed", requestNo);
        return new BankException(m, BankCode.RECORD_FAILED);
    }

    public static InvalidRequestNoException invalidRequestNo(String requestNo) {
        String m = String.format("RequestNo[%s] is invalid", requestNo);
        return new InvalidRequestNoException(m, BankCode.REQUEST_NO_INVALID, requestNo);
    }

    public static BankException errorCaptcha(String requestNo, String captcha) {
        String m = String.format("RequestNo[%s] captcha[%s] error", requestNo, captcha);
        return new BankException(m, BankCode.CAPTCHA_NOT_MATCH);
    }

    public static BankException registerRecordDeadline(String requestNo, LocalDateTime deadline) {
        String m = String.format("Register requestNo[%s] is out of deadline[%s].", requestNo, deadline);
        return new BankException(m, BankCode.REQUEST_OUT_DEADLINE);
    }

    public static IllegalArgumentException unexpectedEnum(Enum<?> e) {
        String m = String.format("%s is unexpected.", e.name());
        return new IllegalArgumentException(m);
    }


}
