package com.example.domain.user;

import com.example.domain.VersionDomain;
import io.army.annotation.*;

import java.time.LocalDateTime;

import static com.example.domain.user.User.NONE_VALUE;


/**
 * created  on 2018/9/19.
 */
@Table(name = "u_user", indexes = {
        @Index(name = "uniq_partner_id", columnList = "partner_no,id_from_partner", unique = true),
        @Index(name = "idx_nick_name_from_partner", columnList = "nick_name_from_partner"),
        @Index(name = "uniq_nick_name", columnList = "nick_name")
}, comment = "用户父表"
)
@Inheritance("userType")
@DiscriminatorValue(NONE_VALUE)
public class User extends VersionDomain {


    public static final int NONE_VALUE = 0;

    public static final int PERSON_VALUE = 100;

    public static final int ENTERPRISE_VALUE = 200;


    public static final int FUNCTION_VALUE = 1000;


    @Column(updatable = false, defaultValue = ZERO, comment = "id")
    private Long id;

    @Column(updatable = false, defaultValue = ZERO, comment = "证件记录id,标识一个自然人或合法组织")
    private Long certificateId;

    @Column(defaultValue = EMPTY, length = 64, comment = "个人或组织的真实名称,此字段加密")
    private String name;

    @Column(defaultValue = EMPTY, length = 20, comment = "昵称,此字段不加密")
    private String nickName;

    @Column(defaultValue = EMPTY, length = 15, comment = "用户注册时,注册数据来源于哪个合作者")
    private String partnerNo;

    @Column(defaultValue = EMPTY, length = 64, comment = "用户在合作者平台用户的id,与 partner_no 联用")
    private String idFromPartner;

    @Column(defaultValue = EMPTY, length = 30, comment = "用户在合作者平台用户的昵称,与 partner_no 联用")
    private String nickNameFromPartner;

    @Column(defaultValue = CURRENT_TIMESTAMP, comment = "用户在合作者平台用户的注册时间,与 partner_no 联用")
    private LocalDateTime createTimeFromPartner;

    @Column(defaultValue = CURRENT_TIMESTAMP, comment = "如果此字段是个未来值,那么用户不可登录,当 status 为 NORMAL 是有效")
    private LocalDateTime unlockTime;

    @Column(defaultValue = ZERO, comment = "用户级别")
    private Integer level;


    @Column(defaultValue = ZERO, comment = "用户类型,这个字段用于区分子表.@see org.qinarmy.jelly.replay.domain.user.UserType")
    private UserType userType;

    @Column(defaultValue = ZERO, comment = "认证状态(实名),@see org.qinarmy.jelly.replay.domain.user.AuthStatus")
    private AuthStatus authStatus;

    @Column(defaultValue = ZERO, comment = "用户类型,@see org.qinarmy.jelly.replay.domain.user.UserStatus")
    private UserStatus status;

    @Column(defaultValue = ZERO, comment = "风险类型")
    private RiskType riskType;

    @Column(defaultValue = EMPTY, comment = "风险评价")
    private String riskComment;


    @Column(defaultValue = ZERO, comment = "乐观锁版本")
    private Integer version;

    @Column(defaultValue = Y, comment = "可见性,用于逻辑删除")
    private Boolean visible;

    @Column(defaultValue = CURRENT_TIMESTAMP)
    private LocalDateTime createTime;

    @Column(defaultValue = CURRENT_TIMESTAMP)
    private LocalDateTime updateTime;


    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public User setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getPartnerNo() {
        return partnerNo;
    }

    public User setPartnerNo(String partnerNo) {
        this.partnerNo = partnerNo;
        return this;
    }

    public String getIdFromPartner() {
        return idFromPartner;
    }

    public User setIdFromPartner(String idFromPartner) {
        this.idFromPartner = idFromPartner;
        return this;
    }

    public String getNickNameFromPartner() {
        return nickNameFromPartner;
    }

    public User setNickNameFromPartner(String nickNameFromPartner) {
        this.nickNameFromPartner = nickNameFromPartner;
        return this;
    }

    public LocalDateTime getCreateTimeFromPartner() {
        return createTimeFromPartner;
    }

    public User setCreateTimeFromPartner(LocalDateTime createTimeFromPartner) {
        this.createTimeFromPartner = createTimeFromPartner;
        return this;
    }

    public UserType getUserType() {
        return userType;
    }

    public User setUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    public UserStatus getStatus() {
        return status;
    }

    public User setStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public AuthStatus getAuthStatus() {
        return authStatus;
    }

    public User setAuthStatus(AuthStatus authStatus) {
        this.authStatus = authStatus;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public User setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public User setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public User setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public RiskType getRiskType() {
        return riskType;
    }

    public User setRiskType(RiskType riskType) {
        this.riskType = riskType;
        return this;
    }

    public String getRiskComment() {
        return riskComment;
    }

    public User setRiskComment(String riskComment) {
        this.riskComment = riskComment;
        return this;
    }

    public LocalDateTime getUnlockTime() {
        return unlockTime;
    }

    public User setUnlockTime(LocalDateTime unlockTime) {
        this.unlockTime = unlockTime;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public User setLevel(Integer level) {
        this.level = level;
        return this;
    }
}
