package io.army.example.bank.domain.user;

import io.army.annotation.Column;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.common.BaseVersionDomain;

import java.time.LocalDateTime;

@Table(name = "r_register_record", indexes = {@Index(name = "uni_request_no", fieldList = "requestNo", unique = true)}
        , comment = "user register request record")
public class RegisterRecord extends BaseVersionDomain<RegisterRecord> {

    @Column
    private Long id;

    @Column(nullable = false, comment = "record handle status")
    private RecordStatus status;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "partner user id")
    private Long partnerId;

    @Column(precision = 30, nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "provide to partner request number")
    private String requestNo;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "record handle time")
    private LocalDateTime handleTime;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "record completion time")
    private LocalDateTime completionTime;


    @Override
    public Long getId() {
        return id;
    }

    public RegisterRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public RegisterRecord setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public RegisterRecord setStatus(RecordStatus status) {
        this.status = status;
        return this;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public RegisterRecord setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public RegisterRecord setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
        return this;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public RegisterRecord setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
        return this;
    }


}
