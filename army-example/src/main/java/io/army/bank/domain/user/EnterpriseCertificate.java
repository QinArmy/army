package io.army.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;

import java.time.LocalDate;

@Table(name = "enterprise_certificate", comment = "bank enterprise certificate")
@DiscriminatorValue(CertificateType.Constant.ENTERPRISE)
public class EnterpriseCertificate extends Certificate {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "enterprise user register day")
    private LocalDate registerDay;


}
