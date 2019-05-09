package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.LOAN_SUCCESS_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_loan_success_record", indexes = {

}, comment = "充值记录表"
)
@DiscriminatorValue(LOAN_SUCCESS_VALUE)
public class LoanSuccessRecord extends BaseRecord {


}
