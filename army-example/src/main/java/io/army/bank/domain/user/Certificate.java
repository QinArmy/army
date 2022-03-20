package io.army.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.common.VersionDomain;

import java.time.LocalDateTime;

@Table(name = "uer_certificate", comment = "bank user certificate")
@Inheritance("certificateType")
public class Certificate extends VersionDomain {

    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;

    @Column
    private CertificateType certificateType;

    @Column(precision = 30, updateMode = UpdateMode.IMMUTABLE, comment = "user certificate number.")
    private String certificateNo;

    @Column(precision = 50, comment = "person or enterprise name")
    private String subjectName;


    @Override
    public Long getId() {
        return id;
    }

    public Certificate setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Certificate setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Certificate setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public Certificate setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public Certificate setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public Certificate setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
        return this;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public Certificate setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
        return this;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public Certificate setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        return this;
    }
}
