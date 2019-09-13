package com.example.domain.plan;

import io.army.annotation.Column;
import io.army.annotation.Table;

import java.math.BigDecimal;

/**
 * created  on 2018/11/19.
 */
@Table(name = "refund_plan", comment = "refund_plan")
public class RefundPlan extends AbstractPlan {


    @Column(defaultValue = DECIMAL_ZERO, updatable = false, precision = 14, scale = 2, comment = "交易金额,此字段不可更新")
    private BigDecimal borrowerPrincipal;

    @Column(defaultValue = DECIMAL_ZERO, updatable = false, precision = 14, scale = 2, comment = "交易金额,此字段不可更新")
    private BigDecimal borrowerInterest;



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

}
