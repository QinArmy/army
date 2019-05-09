package com.example.domain.trade;

import org.qinarmy.army.annotation.DiscriminatorValue;
import org.qinarmy.army.annotation.Table;

import static com.example.domain.trade.BaseRecord.MATCH_FAILURE_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "td_match_failure_record", indexes = {

}, comment = "匹配失败记录表"
)
@DiscriminatorValue(MATCH_FAILURE_VALUE)
public class MatchFailureRecord extends BaseRecord {


}
