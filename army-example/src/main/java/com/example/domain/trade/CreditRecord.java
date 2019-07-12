package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.CREDIT_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_credit_record", indexes = {

}, comment = "借款记录表"
)
@DiscriminatorValue(CREDIT_VALUE)
public class CreditRecord extends BaseRecord {


}
