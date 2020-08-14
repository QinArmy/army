package com.example.fortune.domain.user;

import io.army.annotation.Codec;
import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "u_person", comment = "person user")
@DiscriminatorValue(UserType.Constant.PERSON)
public class Person extends User<Person> {

    @Codec
    @Column(precision = 24, comment = "cipher text of phone of person")
    private String phone;

    public String getPhone() {
        return phone;
    }

    public Person setPhone(String phone) {
        this.phone = phone;
        return this;
    }
}
