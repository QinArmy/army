package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.common.BaseVersionDomain;

import java.time.LocalDateTime;

@Table(name = "u_user", comment = "bank user")
@Inheritance("userType")
@SuppressWarnings("unchecked")
public class BankUser<T extends BankUser<T>> extends BaseVersionDomain<T> {

    @Column
    private BankUserType userType;

    @Column(precision = 50, comment = "user nick name")
    private String nickName;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user certificate table id")
    private Long certificateId;

    @Column(updateMode = UpdateMode.ONLY_NULL, comment = "user register complete time")
    private LocalDateTime completeTime;


    public final BankUserType getUserType() {
        return userType;
    }

    public final T setUserType(BankUserType userType) {
        this.userType = userType;
        return (T) this;
    }

    public final Long getCertificateId() {
        return certificateId;
    }

    public final T setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
        return (T) this;
    }


    public final String getNickName() {
        return nickName;
    }

    public final T setNickName(String nickName) {
        this.nickName = nickName;
        return (T) this;
    }

    public final LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public final BankUser<T> setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
        return this;
    }


}
