package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.REFUND_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_refund_record", indexes = {

}, comment = "回款记录表"
)
@DiscriminatorValue(REFUND_VALUE)
public class RefundRecord extends BaseRecord {


}
