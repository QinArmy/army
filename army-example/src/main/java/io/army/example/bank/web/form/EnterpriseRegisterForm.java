package io.army.example.bank.web.form;

import io.army.example.bank.domain.user.BankUserType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EnterpriseRegisterForm {

    @NotEmpty
    private String requestNo;

    @NotEmpty
    private String name;

    @NotEmpty
    private String phone;

    @NotNull
    private BankUserType userType;

    @NotEmpty
    private String captcha;


    public String getRequestNo() {
        return requestNo;
    }

    public EnterpriseRegisterForm setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public String getName() {
        return name;
    }

    public EnterpriseRegisterForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public EnterpriseRegisterForm setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BankUserType getUserType() {
        return userType;
    }

    public EnterpriseRegisterForm setUserType(BankUserType userType) {
        this.userType = userType;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public EnterpriseRegisterForm setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }


}
