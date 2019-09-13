package com.example.domain.trade;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.LOAN_ROLL_BACK_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_loan_roll_back_record", indexes = {}, comment = "放款失败回退记录"
)
@DiscriminatorValue(LOAN_ROLL_BACK_VALUE)
public class LoanRollBackRecord extends BaseRecord {


}
