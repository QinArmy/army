package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.common.Domain;

import java.time.LocalDateTime;

@Table(name = "captcha", immutable = true
        , indexes = @Index(name = "uni_request_no", fieldList = "requestNo", unique = true)
        , comment = "safe captcha")
public class Captcha extends Domain {

    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "partner user id")
    private Long partnerId;

    @Column(precision = 5, nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "provide to terminate user captcha")
    private String captcha;

    @Column(precision = 30, nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "request number that provide to partner")
    private String requestNo;


    @Override
    public Long getId() {
        return id;
    }

    public Captcha setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public Captcha setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public Captcha setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public Captcha setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Captcha setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }


}
