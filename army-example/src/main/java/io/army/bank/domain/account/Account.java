package io.army.bank.domain.account;

import io.army.annotation.Column;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.bank.domain.user.BankUserType;
import io.army.common.BaseVersionDomain;

import java.math.BigDecimal;

@Table(name = "a_account", comment = "bank user account")
public class Account extends BaseVersionDomain<Account> {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "account type")
    private AccountType accountType;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user id of account")
    private Long userId;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user type of account")
    private BankUserType userType;

    @Column(precision = 14, scale = 2, defaultValue = "0.00", comment = "balance of account")
    private BigDecimal balance;

    @Column(precision = 14, scale = 2, defaultValue = "0.00", comment = "frozen account of account")
    private BigDecimal frozenAmount;


    public final AccountType getAccountType() {
        return accountType;
    }

    public final Account setAccountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public final Long getUserId() {
        return userId;
    }

    public final Account setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public final BankUserType getUserType() {
        return userType;
    }

    public final Account setUserType(BankUserType userType) {
        this.userType = userType;
        return this;
    }

    public final BigDecimal getBalance() {
        return balance;
    }

    public final Account setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public final BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public final Account setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
        return this;
    }


}
