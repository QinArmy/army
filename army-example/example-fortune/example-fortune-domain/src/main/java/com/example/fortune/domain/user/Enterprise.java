package com.example.fortune.domain.user;

import io.army.annotation.Codec;
import io.army.annotation.Column;

//@Table(name = "u_enterprise", comment = "enterprise enterprise")
//@DiscriminatorValue(UserType.Constant.ENTERPRISE)
public class Enterprise extends User<Enterprise> {

    @Column(comment = "primary key of certificate of legal person of enterprise,@see cer_certificate")
    private Long legalCertificateId;

    @Codec
    @Column(precision = 24, comment = "cipher text of enterprise legal phone")
    private String legalPhone;

    public Long getLegalCertificateId() {
        return legalCertificateId;
    }

    public Enterprise setLegalCertificateId(Long legalCertificateId) {
        this.legalCertificateId = legalCertificateId;
        return this;
    }

    public String getLegalPhone() {
        return legalPhone;
    }

    public Enterprise setLegalPhone(String legalPhone) {
        this.legalPhone = legalPhone;
        return this;
    }
}
