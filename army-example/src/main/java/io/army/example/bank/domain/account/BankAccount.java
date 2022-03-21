package io.army.example.bank.domain.account;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.bank.domain.user.BankUserType;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;

@Table(name = "a_account"
        , indexes = {@Index(name = "uni_account_no", fieldList = "accountNo", unique = true)}
        , comment = "bank user account")
public class BankAccount extends BaseVersionDomain<BankAccount> {

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "account type")
    private AccountType accountType;

    @Column(nullable = false, precision = 40, updateMode = UpdateMode.IMMUTABLE, comment = "provide to partner account number")
    private String accountNo;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user id of account")
    private Long userId;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user type of account")
    private BankUserType userType;

    @Column(nullable = false, precision = 14, scale = 2, defaultValue = "0.00", comment = "balance of account")
    private BigDecimal balance;

    @Column(nullable = false, precision = 14, scale = 2, defaultValue = "0.00", comment = "frozen account of account")
    private BigDecimal frozenAmount;


    public final AccountType getAccountType() {
        return accountType;
    }

    public final BankAccount setAccountType(AccountType accountType) {
        this.accountType = accountType;
        return this;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public BankAccount setAccountNo(String accountNo) {
        this.accountNo = accountNo;
        return this;
    }

    public final Long getUserId() {
        return userId;
    }

    public final BankAccount setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public final BankUserType getUserType() {
        return userType;
    }

    public final BankAccount setUserType(BankUserType userType) {
        this.userType = userType;
        return this;
    }

    public final BigDecimal getBalance() {
        return balance;
    }

    public final BankAccount setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public final BigDecimal getFrozenAmount() {
        return frozenAmount;
    }

    public final BankAccount setFrozenAmount(BigDecimal frozenAmount) {
        this.frozenAmount = frozenAmount;
        return this;
    }


}
