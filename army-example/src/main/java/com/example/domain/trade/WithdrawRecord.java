package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.WITHDRAW_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_charge_record", indexes = {

}, comment = "提现记录"
)
@DiscriminatorValue(WITHDRAW_VALUE)
public class WithdrawRecord extends BaseRecord {


}
