package io.army.example.bank.web.form;

import javax.validation.constraints.NotEmpty;

public class RegisterForm {

    @NotEmpty
    private String requestNo;


    @NotEmpty
    private String captcha;

    public String getRequestNo() {
        return requestNo;
    }

    public RegisterForm setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public RegisterForm setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }


}
