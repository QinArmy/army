package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.REPAY_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_repay_record", indexes = {

}, comment = "还款记录表"
)
@DiscriminatorValue(REPAY_VALUE)
public class RepayRecord extends BaseRecord {


}
