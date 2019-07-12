package com.example.domain.account;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.account.Account.BALANCE_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "a_balance_account", comment = "余额账户表")
@DiscriminatorValue(BALANCE_VALUE)
public class BalanceAccount extends Account {


}
