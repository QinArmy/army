package io.army.example.bank.web.form;

import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.CertificateType;
import io.army.example.common.Criteria;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public final class PersonRegisterForm extends Criteria {

    @NotNull
    private String partnerUserNo;

    @NotEmpty
    private String name;

    @NotNull
    private CertificateType certificateType;

    @NotEmpty
    private String certificateNo;

    @NotEmpty
    private String phone;

    @NotNull
    private BankAccountType accountType;

    @NotEmpty
    private String captcha;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getPartnerUserNo() {
        return partnerUserNo;
    }

    public void setPartnerUserNo(String partnerUserNo) {
        this.partnerUserNo = partnerUserNo;
    }

    public BankAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(BankAccountType accountType) {
        this.accountType = accountType;
    }
}
