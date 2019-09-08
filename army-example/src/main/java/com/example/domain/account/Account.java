package com.example.domain.account;

import com.example.domain.VersionDomain;
import io.army.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.domain.account.Account.NONE_VALUE;


/**
 * created  on 2018/9/27.
 */
@Table(name = "a_account", indexes = {
        @Index(name = "idx_user_id_account_type", columnList = "user_id,account_type", unique = true)
}, comment = "账户父表"
)
@Inheritance("accountType")
@DiscriminatorValue(NONE_VALUE)
public class Account extends VersionDomain {

    public static final int NONE_VALUE = 0;

    public static final int BALANCE_VALUE = 100;

    public static final int INVEST_VALUE = 200;

    public static final int LENDER_VALUE = 300;

    public static final int BORROWER_VALUE = 400;

    public static final int FUNCTION_VALUE = 3000;


    @Column(defaultValue = ZERO, updatable = false, comment = "id")
    private Long id;

    @Column(defaultValue = ZERO, updatable = false, comment = "用户id")
    private Long userId;

    @Column(defaultValue = ZERO, updatable = false, comment = "账户类型")
    private AccountType accountType;

    @Column(defaultValue = ZERO, updatable = false, comment = "账户状态")
    private AccountStatus status;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2
            , comment = "账户余额,对与余额是否可提现要与 withdrawable 联用,对于是否允许用户操作此账户的资金与 manualUse 联用")
    private BigDecimal balance;

    @Column(defaultValue = N, updatable = false, comment = "是否可提现,这个字段决定相应账户余额是否可提现(balance)")
    private Boolean withdrawable;

    @Column(defaultValue = N, updatable = false, comment = "是否允许用户操作这个账户资金(balance),特指是否允许用户使用此账户的资金进行投资购买等")
    private Boolean manualUse;

    @Column(defaultValue = ZERO, updatable = false, comment = "用户请求流水号")
    private String requestNo;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "账户当前收益")
    private BigDecimal yield;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "账户当前债务")
    private BigDecimal debt;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "账户累计收益")
    private BigDecimal accumulativeYield;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "账户累计债务")
    private BigDecimal accumulativeDebt;

    @Column(defaultValue = NOW, comment = "如果此字段是个未来值,那么账户不可用,当 status 为 NORMAL 是有效")
    private LocalDateTime unlockTime;


    @Column(defaultValue = ZERO, comment = "乐观锁版本")
    private Integer version;

    @Column(defaultValue = Y, comment = "可见性,用于逻辑删除")
    private Boolean visible;

    @Column(defaultValue = NOW, comment = "向第三方请求开户的时间")
    private LocalDateTime requestTime;

    @Column(defaultValue = NOW, comment = "第三方受理开户的时间")
    private LocalDateTime acceptTime;

    @Column(defaultValue = NOW, comment = "第三方开户成功的时间")
    private LocalDateTime finishTime;

    @Column(defaultValue = NOW)
    private LocalDateTime createTime;

    @Column(defaultValue = NOW)
    private LocalDateTime updateTime;


    public Long getId() {
        return id;
    }

    public Account setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Account setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Account setAccountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Account setStatus(AccountStatus status) {
        this.status = status;
        return this;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public Boolean getWithdrawable() {
        return withdrawable;
    }

    public Account setWithdrawable(Boolean withdrawable) {
        this.withdrawable = withdrawable;
        return this;
    }

    public Boolean getManualUse() {
        return manualUse;
    }

    public Account setManualUse(Boolean manualUse) {
        this.manualUse = manualUse;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public Account setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public Account setYield(BigDecimal yield) {
        this.yield = yield;
        return this;
    }

    public BigDecimal getDebt() {
        return debt;
    }

    public Account setDebt(BigDecimal debt) {
        this.debt = debt;
        return this;
    }

    public BigDecimal getAccumulativeYield() {
        return accumulativeYield;
    }

    public Account setAccumulativeYield(BigDecimal accumulativeYield) {
        this.accumulativeYield = accumulativeYield;
        return this;
    }

    public BigDecimal getAccumulativeDebt() {
        return accumulativeDebt;
    }

    public Account setAccumulativeDebt(BigDecimal accumulativeDebt) {
        this.accumulativeDebt = accumulativeDebt;
        return this;
    }

    public LocalDateTime getUnlockTime() {
        return unlockTime;
    }

    public Account setUnlockTime(LocalDateTime unlockTime) {
        this.unlockTime = unlockTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Account setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public Account setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public Account setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public LocalDateTime getAcceptTime() {
        return acceptTime;
    }

    public Account setAcceptTime(LocalDateTime acceptTime) {
        this.acceptTime = acceptTime;
        return this;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public Account setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Account setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Account setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
