package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;

import java.time.LocalDate;

@Table(name = "u_person", comment = "bank person user")
@DiscriminatorValue(BankUserType.Constant.PERSON)
public class Person extends BankUser<Person> {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "the user id that user form.")
    private Long fromPartnerId;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "the user type that user form.")
    private BankUserType fromPartnerType;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user birthday")
    private LocalDate birthday;

    @Column(precision = 20, comment = "user phone number")
    private String phone;


    public final LocalDate getBirthday() {
        return birthday;
    }

    public final Person setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public final String getPhone() {
        return phone;
    }

    public final Person setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public final Long getFromPartnerId() {
        return fromPartnerId;
    }

    public final Person setFromPartnerId(Long fromPartnerId) {
        this.fromPartnerId = fromPartnerId;
        return this;
    }

    public final BankUserType getFromPartnerType() {
        return fromPartnerType;
    }

    public final Person setFromPartnerType(BankUserType fromPartnerType) {
        this.fromPartnerType = fromPartnerType;
        return this;
    }


}
