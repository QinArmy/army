package com.example.fortune.domain.user;

import com.example.fortune.domain.VersionDomain;
import io.army.annotation.*;
import org.qinarmy.depository.CertificateType;

import java.time.LocalDateTime;


@Table(name = "cer_certificate", comment = "certificate"
        , indexes = {
        @Index(name = "uni_certificate_no_certificate_type", columnList = {"certificate_no", "certificate_type"}, unique = true)})
@Inheritance("certificate_type")
@SuppressWarnings("unchecked")
public class Certificate<T extends Certificate<T>> extends VersionDomain {

    @Column(comment = "@see org.qinarmy.depository.mock.domain.user.CertificateType")
    private CertificateType certificateType;

    @Codec
    @Column(updatable = false, precision = 44, comment = "cipher text of number of certificate")
    private String certificateNo;

    @Column(updatable = false, defaultValue = NOW, comment = "register time same as create time")
    private LocalDateTime registerTime;

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public T setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
        return (T) this;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public T setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
        return (T) this;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public T setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
        return (T) this;
    }
}
