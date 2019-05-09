package com.example.domain.account;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.account.Account.FUNCTION_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "a_function_account", comment = "功能账户表")
@DiscriminatorValue(FUNCTION_VALUE)
public class FunctionAccount extends Account {


}
