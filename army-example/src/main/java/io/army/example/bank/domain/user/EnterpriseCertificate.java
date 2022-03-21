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


    public LocalDate getRegisterDay() {
        return registerDay;
    }

    public EnterpriseCertificate setRegisterDay(LocalDate registerDay) {
        this.registerDay = registerDay;
        return this;
    }


}
