package io.army.example.bank.web.form;

import io.army.example.bank.domain.user.BankUserType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class EnterpriseRegisterForm {

    @NotEmpty
    private String name;

    @NotEmpty
    private String phone;

    @NotNull
    private BankUserType userType;

    @NotEmpty
    private String captcha;


}
