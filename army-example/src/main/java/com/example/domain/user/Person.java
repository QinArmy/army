package com.example.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.struct.enumeration.Gender;

import java.time.LocalDate;

import static com.example.domain.user.User.PERSON_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "u_person", comment = "个人用户表")
@DiscriminatorValue(PERSON_VALUE)
public class Person extends User {

    @Column(defaultValue = ZERO, comment = "性别")
    private Gender gender;

    @Column(defaultValue = ZERO_DATE, comment = "生日")
    private LocalDate birthday;

    public Gender getGender() {
        return gender;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public Person setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }
}
