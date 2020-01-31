package com.example.domain.trade;

import com.example.domain.ClientType;
import com.example.domain.VersionDomain;
import io.army.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.example.domain.trade.BaseRecord.NONE_VALUE;


/**
 * created  on 2018/9/27.
 */
@Table(name = "td_base_record", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_account_id", columnList = "account_id"),
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "uniq_request_no", columnList = "request_no", unique = true),
}, comment = "交易记录父表"
)
@Inheritance("trade_type")
public class BaseRecord extends VersionDomain {

    public static final int NONE_VALUE = 0;

    public static final int CREDIT_VALUE = 10;

    public static final int CHARGE_VALUE = 20;

    public static final int REFUND_VALUE = 30;

    public static final int MATCH_FAILURE_VALUE = 40;

    public static final int LOAN_ROLL_BACK_VALUE = 60;


    public static final int DEBIT_VALUE = 5010;

    public static final int WITHDRAW_VALUE = 5020;

    public static final int REPAY_VALUE = 5030;

    public static final int LOAN_SUCCESS_VALUE = 5040;

    @Column(defaultValue = ZERO, updatable = false, comment = "id")
    private Long id;

    @Column(defaultValue = ZERO, updatable = false, comment = "交易类型")
    private TradeType tradeType;

    @Column(defaultValue = ZERO, comment = "交易状态,@see org.qinarmy.jelly.replay.domain.trade.TradeStatus")
    private TradeStatus status;

    @Column(defaultValue = ZERO, comment = "交易子状态状态,由子表定义,@see org.qinarmy.jelly.replay.domain.trade.TradeSubStatus")
    private TradeSubStatus subStatus;

    @Column(defaultValue = ZERO, updatable = false, comment = "用户id")
    private Long userId;

    @Column(defaultValue = ZERO, updatable = false, comment = "产品id")
    private Long productId;

    @Column(defaultValue = ZERO, comment = "银行卡记录id")
    private Long bankCardId;

    @Column(defaultValue = ZERO, updatable = false, comment = "账户id")
    private Long accountId;

    @Column(comment = "产品名")
    private String productName;

    @Column(defaultValue = ZERO, updatable = false, comment = "交易提供者")
    private TradeProvider provider;

    @Column(comment = "描述,给用户看的查询")
    private String desc;

    @Column(comment = "标签,对交易记录标识")
    private String label;

    @Column(defaultValue = DECIMAL_ZERO, updatable = false, precision = 14, scale = 2, comment = "交易金额,此字段不可更新")
    private BigDecimal amount;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "向用户收取的交易费用")
    private BigDecimal fee;

    @Column(defaultValue = DECIMAL_ZERO, precision = 14, scale = 2, comment = "给用户做的减免")
    private BigDecimal relief;


    @Column(defaultValue = ZERO, updatable = false, comment = "客户端类型")
    private ClientType clientType;

    @Column(defaultValue = NOW, comment = "交易请求时间")
    private LocalDateTime tradeRequestTime;

    @Column(defaultValue = NOW, comment = "交易受理时间")
    private LocalDateTime tradeAcceptTime;

    @Column(defaultValue = NOW, comment = "交易完成时间")
    private LocalDateTime tradeFinishTime;

    @Column(comment = "第三方返回的消息")
    private String tradeMsg;

    @Column(comment = "第三方返回的失败原因")
    private String failureReason;

    @Column(comment = "请求第三方的序列号")
    private String requestNo;

    @Column(defaultValue = NOW, comment = "关闭时间,当用户超时未完成交易的时间")
    private LocalDateTime closeTime;

    @Column(defaultValue = NOW, comment = "记录最近一次更新 status 的时间")
    private LocalDateTime handleTime;

    @Column(defaultValue = NOW, comment = "当记录被需要异步处理时的时间,为特别的记录预留")
    private LocalDateTime startTime;

    @Column(defaultValue = NOW, comment = "定时任务能够扫瞄到的时间")
    private LocalDateTime nextTime;

    @Column(defaultValue = N, comment = "记录是否已发送过mq")
    private Boolean asyncMsg;

    @Column(comment = "备注,用于给用户填写")
    private String remark;

    @Column(defaultValue = N, comment = "标识记录是否已和第三方进行核对")
    private Boolean checking;

    @Column(defaultValue = DECIMAL_ZERO, comment = "完成此记录需要支付给第三方的成本")
    private BigDecimal cost;

    @Column(defaultValue = DECIMAL_ZERO, comment = "完成此记录应向第三方或用户收取的费用")
    private BigDecimal revenue;



    public Long getId() {
        return id;
    }

    public BaseRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BaseRecord setTradeType(TradeType tradeType) {
        this.tradeType = tradeType;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public BaseRecord setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getProductId() {
        return productId;
    }

    public BaseRecord setProductId(Long productId) {
        this.productId = productId;
        return this;
    }

    public Long getBankCardId() {
        return bankCardId;
    }

    public BaseRecord setBankCardId(Long bankCardId) {
        this.bankCardId = bankCardId;
        return this;
    }

    public Long getAccountId() {
        return accountId;
    }

    public BaseRecord setAccountId(Long accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public BaseRecord setProductName(String productName) {
        this.productName = productName;
        return this;
    }


    public String getDesc() {
        return desc;
    }

    public BaseRecord setDesc(String desc) {
        this.desc = desc;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public BaseRecord setLabel(String label) {
        this.label = label;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BaseRecord setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BaseRecord setFee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public BigDecimal getRelief() {
        return relief;
    }

    public BaseRecord setRelief(BigDecimal relief) {
        this.relief = relief;
        return this;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public BaseRecord setStatus(TradeStatus status) {
        this.status = status;
        return this;
    }

    public TradeSubStatus getSubStatus() {
        return subStatus;
    }

    public BaseRecord setSubStatus(TradeSubStatus subStatus) {
        this.subStatus = subStatus;
        return this;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public BaseRecord setClientType(ClientType clientType) {
        this.clientType = clientType;
        return this;
    }

    public LocalDateTime getTradeRequestTime() {
        return tradeRequestTime;
    }

    public BaseRecord setTradeRequestTime(LocalDateTime tradeRequestTime) {
        this.tradeRequestTime = tradeRequestTime;
        return this;
    }

    public LocalDateTime getTradeAcceptTime() {
        return tradeAcceptTime;
    }

    public BaseRecord setTradeAcceptTime(LocalDateTime tradeAcceptTime) {
        this.tradeAcceptTime = tradeAcceptTime;
        return this;
    }

    public LocalDateTime getTradeFinishTime() {
        return tradeFinishTime;
    }

    public BaseRecord setTradeFinishTime(LocalDateTime tradeFinishTime) {
        this.tradeFinishTime = tradeFinishTime;
        return this;
    }

    public String getTradeMsg() {
        return tradeMsg;
    }

    public BaseRecord setTradeMsg(String tradeMsg) {
        this.tradeMsg = tradeMsg;
        return this;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public BaseRecord setCloseTime(LocalDateTime closeTime) {
        this.closeTime = closeTime;
        return this;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public BaseRecord setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public BaseRecord setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getNextTime() {
        return nextTime;
    }

    public BaseRecord setNextTime(LocalDateTime nextTime) {
        this.nextTime = nextTime;
        return this;
    }

    public Boolean getAsyncMsg() {
        return asyncMsg;
    }

    public BaseRecord setAsyncMsg(Boolean asyncMsg) {
        this.asyncMsg = asyncMsg;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public BaseRecord setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Boolean getChecking() {
        return checking;
    }

    public BaseRecord setChecking(Boolean checking) {
        this.checking = checking;
        return this;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public BaseRecord setCost(BigDecimal cost) {
        this.cost = cost;
        return this;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public BaseRecord setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
        return this;
    }


    public String getFailureReason() {
        return failureReason;
    }

    public BaseRecord setFailureReason(String failureReason) {
        this.failureReason = failureReason;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public BaseRecord setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public TradeProvider getProvider() {
        return provider;
    }

    public BaseRecord setProvider(TradeProvider provider) {
        this.provider = provider;
        return this;
    }
}
