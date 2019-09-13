package com.example.domain.account;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import java.math.BigDecimal;

import static com.example.domain.account.Account.INVEST_VALUE;

/**
 * 投资账户的资金只能用投资,不可提现。
 * created  on 2018/9/27.
 */
@Table(name = "a_invest_account", comment = "投资账户表")
@DiscriminatorValue(INVEST_VALUE)
public class InvestAccount extends Account {

    @Column(defaultValue = DECIMAL_ZERO, comment = "投资金额")
    private BigDecimal investAmount;
}
