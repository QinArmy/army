package com.example.domain.plan;

import io.army.annotation.Column;
import io.army.annotation.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * created  on 2018/11/19.
 */
@Table(name = "refund_plan")
public class RefundPlan extends AbstractPlan {


    @Column(defaultValue = DECIMAL_ZERO, updatable = false, precision = 14, scale = 2, comment = "交易金额,此字段不可更新")
    private BigDecimal borrowerPrincipal;

    @Column(defaultValue = DECIMAL_ZERO, updatable = false, precision = 14, scale = 2, comment = "交易金额,此字段不可更新")
    private BigDecimal borrowerInterest;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;

    @Column
    private Integer version;


    public BigDecimal getBorrowerPrincipal() {
        return borrowerPrincipal;
    }

    public RefundPlan setBorrowerPrincipal(BigDecimal borrowerPrincipal) {
        this.borrowerPrincipal = borrowerPrincipal;
        return this;
    }

    public BigDecimal getBorrowerInterest() {
        return borrowerInterest;
    }

    public RefundPlan setBorrowerInterest(BigDecimal borrowerInterest) {
        this.borrowerInterest = borrowerInterest;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public RefundPlan setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public RefundPlan setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public RefundPlan setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public RefundPlan setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
