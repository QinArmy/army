package com.example.domain.yesterday;


import com.example.domain.Domain;
import io.army.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * created  on 2018/9/27.
 */
@Table(name = "yd_record", indexes = {@Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_date", columnList = "date")
},
        comment = "昨日收益/债务记录父表")
@Inheritance("recordType")
@Immutable
public class YesterdayRecord extends Domain {

    @Column(defaultValue = ZERO, updatable = false, comment = "id")
    private Long id;

    @Column(defaultValue = ZERO, updatable = false, comment = "用户id")
    private Long userId;

    @Column(defaultValue = ZERO, updatable = false, comment = "账户id")
    private Long accountId;

    @Column(defaultValue = EMPTY, updatable = false, comment = "收益 label")
    private String yieldLabel;

    @Column(defaultValue = EMPTY, updatable = false, comment = "账务 label")
    private String debtLabel;

    @Column(defaultValue = ZERO, updatable = false, comment = "记录类型,用于区分子表")
    private YesterdayRecordType recordType;

    @Column(defaultValue = CURRENT_DATE, updatable = false, comment = "日期")
    private LocalDate date;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "昨日收益")
    private BigDecimal yield;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "昨日债务")
    private BigDecimal debt;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;

    @Column
    private Integer version;


    public Long getId() {
        return id;
    }

    public YesterdayRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public YesterdayRecord setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getAccountId() {
        return accountId;
    }

    public YesterdayRecord setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public YesterdayRecordType getRecordType() {
        return recordType;
    }

    public YesterdayRecord setRecordType(YesterdayRecordType recordType) {
        this.recordType = recordType;
        return this;
    }

    public String getYieldLabel() {
        return yieldLabel;
    }

    public YesterdayRecord setYieldLabel(String yieldLabel) {
        this.yieldLabel = yieldLabel;
        return this;
    }

    public String getDebtLabel() {
        return debtLabel;
    }

    public YesterdayRecord setDebtLabel(String debtLabel) {
        this.debtLabel = debtLabel;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public YesterdayRecord setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public YesterdayRecord setYield(BigDecimal yield) {
        this.yield = yield;
        return this;
    }

    public BigDecimal getDebt() {
        return debt;
    }

    public YesterdayRecord setDebt(BigDecimal debt) {
        this.debt = debt;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public YesterdayRecord setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public YesterdayRecord setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public YesterdayRecord setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public YesterdayRecord setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
