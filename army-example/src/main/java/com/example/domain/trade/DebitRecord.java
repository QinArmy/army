package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.DEBIT_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_debit_record", indexes = {
}, comment = "出借记录表,用于记录用户的出借情况"
)
@DiscriminatorValue(DEBIT_VALUE)
public class DebitRecord extends BaseRecord {


}
