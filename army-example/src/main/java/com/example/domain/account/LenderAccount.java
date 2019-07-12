package com.example.domain.account;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.account.Account.LENDER_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "a_lender_account", comment = "出借人账户表")
@DiscriminatorValue(LENDER_VALUE)
public class LenderAccount extends Account {


}
