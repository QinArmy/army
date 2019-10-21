package com.example.domain.trade;

import io.army.struct.CodeEnum;

import java.util.Map;

/**
 * 交易记录类型
 * created  on 2018/9/27.
 *
 * @see BaseRecord
 */
public enum TradeType implements CodeEnum {

    NONE(BaseRecord.NONE_VALUE, "无", BaseRecord.class),

    CREDIT(BaseRecord.CREDIT_VALUE, "借款", CreditRecord.class),

    CHARGE(BaseRecord.CHARGE_VALUE, "充值记录", ChargeRecord.class),

    REFUND(BaseRecord.REFUND_VALUE, "回款", RefundRecord.class),

    MATCH_FAILURE(BaseRecord.MATCH_FAILURE_VALUE, "匹配失败退回", MatchFailureRecord.class),

    LOAN_ROLL_BACK(BaseRecord.LOAN_ROLL_BACK_VALUE, "放款失败退回", LoanRollBackRecord.class),


    DEBIT(BaseRecord.DEBIT_VALUE, "出借", DebitRecord.class),

    WITHDRAW(BaseRecord.WITHDRAW_VALUE, "提现", WithdrawRecord.class),

    REPAY(BaseRecord.REPAY_VALUE, "还款", RepayRecord.class),

    LOAN_SUCCESS(BaseRecord.LOAN_SUCCESS_VALUE, "放款到银行卡成功", LoanSuccessRecord.class),

    ;


    private final int code;

    private final String display;

    private final Class<? extends BaseRecord> type;

    private static final Map<Integer, TradeType> CODE_MAP = CodeEnum.getCodeMap(TradeType.class);


    public static TradeType resolve(int code) {
        return CODE_MAP.get(code);
    }

    TradeType(int code, String display, Class<? extends BaseRecord> type) {
        this.code = code;
        this.display = display;
        this.type = type;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String display() {
        return display;
    }

    public Class<? extends BaseRecord> type() {
        return type;
    }


}
