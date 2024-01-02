/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.example.bank.domain.account;

import io.army.annotation.*;
import io.army.example.bank.domain.user.BankUserType;
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;

@Table(name = "a_account"
        , indexes = {
        @Index(name = "uni_account_no", fieldList = "accountNo", unique = true)
        , @Index(name = "uni_user_id_account_type", fieldList = {"userId", "accountType"}, unique = true)}
        , comment = "bank user account")
public class BankAccount extends BaseVersionDomain<BankAccount> {

    @Column
    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    private Long id;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "account type")
    private BankAccountType accountType;

    @Column(nullable = false, precision = 40, updateMode = UpdateMode.IMMUTABLE, comment = "provide to partner account number")
    @Generator(value = SNOWFLAKE, params = {
            @Param(name = START_TIME, value = startTime), @Param(name = DEPEND, value = "userId")})
    private String accountNo;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user id of account")
    private Long userId;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user type of account")
    private BankUserType userType;

    @Column(nullable = false, precision = 14, scale = 2, defaultValue = "0.00", comment = "balance of account")
    private BigDecimal balance;

    @Column(nullable = false, precision = 14, scale = 2, defaultValue = "0.00", comment = "frozen account of account")
    private BigDecimal frozenAmount;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "r_register_record primary key")
    private Long registerRecordId;


    public final BankAccountType getAccountType() {
        return accountType;
    }

    public final BankAccount setAccountType(BankAccountType accountType) {
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

    @Override
    public Long getId() {
        return id;
    }

    public BankAccount setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getRegisterRecordId() {
        return registerRecordId;
    }

    public BankAccount setRegisterRecordId(Long registerRecordId) {
        this.registerRecordId = registerRecordId;
        return this;
    }


}
