package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.WITHDRAW_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_withdraw_record", indexes = {

}, comment = "提现记录"
)
@DiscriminatorValue(WITHDRAW_VALUE)
public class WithdrawRecord extends BaseRecord {


}
