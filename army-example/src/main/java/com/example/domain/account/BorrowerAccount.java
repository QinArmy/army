package com.example.domain.account;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.account.Account.BORROWER_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "a_borrower_account", comment = "借款人账户表")
@DiscriminatorValue(BORROWER_VALUE)
public class BorrowerAccount extends Account {


}
