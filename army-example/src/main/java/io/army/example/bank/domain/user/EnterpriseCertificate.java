package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;

import java.time.LocalDate;

@Table(name = "enterprise_certificate", comment = "bank enterprise certificate")
@DiscriminatorValue(CertificateType.Constant.ENTERPRISE)
public class EnterpriseCertificate extends Certificate<EnterpriseCertificate> {

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "enterprise user register day")
    private LocalDate registerDay;

    @Column(precision = 40, nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "enterprise Unified social credit code")
    private String creditCode;

    @Column(nullable = false, defaultValue = "0", comment = "enterprise legal person name")
    private Long legalPersonCertificateId;


    public LocalDate getRegisterDay() {
        return registerDay;
    }

    public EnterpriseCertificate setRegisterDay(LocalDate registerDay) {
        this.registerDay = registerDay;
        return this;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public EnterpriseCertificate setCreditCode(String creditCode) {
        this.creditCode = creditCode;
        return this;
    }

    public Long getLegalPersonCertificateId() {
        return legalPersonCertificateId;
    }

    public EnterpriseCertificate setLegalPersonCertificateId(Long legalPersonCertificateId) {
        this.legalPersonCertificateId = legalPersonCertificateId;
        return this;
    }


}
