package io.army.example.bank.web.form;

import io.army.example.bank.domain.user.BankUserType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
    private String certificateNo;

    @NotEmpty
    private String creditCode;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registerDay;

    @NotEmpty
    private String city;

    @NotEmpty
    private String legalPerson;

    @NotEmpty
    private String legalPersonCertificateNo;

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


    public String getCertificateNo() {
        return certificateNo;
    }

    public EnterpriseRegisterForm setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
        return this;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public EnterpriseRegisterForm setCreditCode(String creditCode) {
        this.creditCode = creditCode;
        return this;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public EnterpriseRegisterForm setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
        return this;
    }


    public String getLegalPersonCertificateNo() {
        return legalPersonCertificateNo;
    }

    public EnterpriseRegisterForm setLegalPersonCertificateNo(String legalPersonCertificateNo) {
        this.legalPersonCertificateNo = legalPersonCertificateNo;
        return this;
    }

    public LocalDate getRegisterDay() {
        return registerDay;
    }

    public EnterpriseRegisterForm setRegisterDay(LocalDate registerDay) {
        this.registerDay = registerDay;
        return this;
    }

    public String getCity() {
        return city;
    }

    public EnterpriseRegisterForm setCity(String city) {
        this.city = city;
        return this;
    }


}
