package io.army.example.pill.domain;


import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.example.pill.struct.UserType;

import java.time.LocalDate;

@Table(name = "u_person", comment = "person user")
@DiscriminatorValue(UserType.Constant.PERSON)
public class PillPerson extends PillUser {

    @Column(comment = "user's birthday")
    private LocalDate birthday;

    @Column(precision = 20, comment = "user's phone")
    private String phone;

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}

