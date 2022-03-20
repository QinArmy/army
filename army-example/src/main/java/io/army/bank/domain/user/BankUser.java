package io.army.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.common.BaseVersionDomain;

@Table(name = "u_user", comment = "bank user")
@SuppressWarnings("unchecked")
public class BankUser<T extends BankUser<T>> extends BaseVersionDomain<T> {

    @Column
    private BankUserType bankUserType;

    @Column(precision = 50, comment = "user nick name")
    private String nickName;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user certificate table id")
    private Long certificateId;


    public final BankUserType getBankUserType() {
        return bankUserType;
    }

    public final T setBankUserType(BankUserType bankUserType) {
        this.bankUserType = bankUserType;
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


}
