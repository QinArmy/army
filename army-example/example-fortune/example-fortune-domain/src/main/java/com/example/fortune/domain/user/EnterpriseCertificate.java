package com.example.fortune.domain.user;

import io.army.annotation.Codec;
import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import org.qinarmy.depository.CertificateType;

@Table(name = "cer_enterprise", comment = "certificate of enterprise")
@DiscriminatorValue(CertificateType.Constant.ENTERPRISE_ID)
public class EnterpriseCertificate extends Certificate<EnterpriseCertificate> {

    @Codec
    @Column(precision = 44, comment = "cipher text of enterprise name")
    private String enterpriseName;

    @Column(comment = "primary key of certificate of legal person of enterprise,@see cer_certificate")
    private Long legalCertificateId;

    @Codec
    @Column(comment = "cipher text of phone of legal person")
    private String legalPersonPhone;

    public Long getLegalCertificateId() {
        return legalCertificateId;
    }

    public EnterpriseCertificate setLegalCertificateId(Long legalCertificateId) {
        this.legalCertificateId = legalCertificateId;
        return this;
    }

    public String getLegalPersonPhone() {
        return legalPersonPhone;
    }

    public EnterpriseCertificate setLegalPersonPhone(String legalPersonPhone) {
        this.legalPersonPhone = legalPersonPhone;
        return this;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public EnterpriseCertificate setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
        return this;
    }
}
