package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.CHARGE_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_charge_record", indexes = {

}, comment = "充值记录表"
)
@DiscriminatorValue(CHARGE_VALUE)
public class ChargeRecord extends BaseRecord {


}
