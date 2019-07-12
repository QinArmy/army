package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

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
