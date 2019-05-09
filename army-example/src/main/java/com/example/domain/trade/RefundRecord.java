package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

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
