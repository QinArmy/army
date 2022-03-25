package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;

import java.time.LocalDate;

@Table(name = "u_person", comment = "bank person user")
@DiscriminatorValue(BankUserType.Constant.PERSON)
public class Person extends BankUser<Person> {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "the user type that user form.")
    private BankUserType fromPartnerType;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user birthday")
    private LocalDate birthday;

    @Column(nullable = false, precision = 20, comment = "user phone number")
    private String phone;


    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        //return this;
    }

    public String getPhone() {
        return phone;
    }

    public Person setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BankUserType getFromPartnerType() {
        return fromPartnerType;
    }

    public Person setFromPartnerType(BankUserType fromPartnerType) {
        this.fromPartnerType = fromPartnerType;
        return this;
    }


}
