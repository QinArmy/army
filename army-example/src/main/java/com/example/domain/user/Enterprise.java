package com.example.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import java.time.LocalDateTime;

import static com.example.domain.user.User.ENTERPRISE_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "u_enterprise_user", comment = "企业用户表")
@DiscriminatorValue(ENTERPRISE_VALUE)
public class Enterprise extends User {

    @Column(defaultValue = NOW, updatable = false, comment = "公司注册时间")
    private LocalDateTime registerTime;

    @Column(precision = 64, comment = "公司法人代表")
    private String legalPerson;

    @Column(precision = 64, comment = "公司联系人")
    private String contacter;

    @Column(precision = 20, comment = "公司联系人手机")
    private String contacterPhone;

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public Enterprise setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
        return this;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public Enterprise setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
        return this;
    }

    public String getContacter() {
        return contacter;
    }

    public Enterprise setContacter(String contacter) {
        this.contacter = contacter;
        return this;
    }

    public String getContacterPhone() {
        return contacterPhone;
    }

    public Enterprise setContacterPhone(String contacterPhone) {
        this.contacterPhone = contacterPhone;
        return this;
    }
}
