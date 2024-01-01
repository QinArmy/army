package io.army.example.bank.domain.user;

import io.army.annotation.*;
import io.army.example.common.BaseVersionDomain;

@Table(name = "uer_certificate"
        , indexes = {@Index(name = "uni_certificate_no_type", fieldList = {"certificateNo", "certificateType"}, unique = true)}
        , comment = "bank user certificate")
@Inheritance("certificateType")
@SuppressWarnings("unchecked")
public class Certificate<T extends Certificate<T>> extends BaseVersionDomain<T> {


    public static Certificate<?> create() {
        return new Certificate<>();
    }

    @SuppressWarnings("unchecked")
    public static final Class<Certificate<?>> CLASS = (Class<Certificate<?>>) ((Class<?>) Certificate.class);


    @Column
    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    private Long id;


    @Column(nullable = false)
    private CertificateType certificateType;

    @Column(precision = 30, nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user certificate number.")
    private String certificateNo;

    @Column(precision = 50, nullable = false, comment = "person or enterprise name")
    private String subjectName;


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

    public String getSubjectName() {
        return subjectName;
    }

    public T setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        return (T) this;
    }


    @Override
    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }


}
