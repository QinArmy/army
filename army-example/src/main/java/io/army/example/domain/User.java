package io.army.example.domain;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;
import io.army.example.VersionDomain;
import io.army.example.struct.IdentityType;
import io.army.example.struct.UserType;

import java.time.LocalDateTime;

@Table(name = "u_user", comment = "user")
@Inheritance("userType")
public class User extends VersionDomain {

    @Column
    private Long id;

    @Column
    private UserType userType;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;

    @Column(precision = 64, defaultValue = "''", comment = "user nick name")
    private String nickName;

    @Column(comment = "user identity type", updateMode = UpdateMode.ONLY_NULL)
    private IdentityType identityType;

    @Column(defaultValue = "0", comment = "user identity id", updateMode = UpdateMode.ONLY_NULL)
    private Long identityId;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public void setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
    }

    public Long getIdentityId() {
        return identityId;
    }

    public void setIdentityId(Long identityId) {
        this.identityId = identityId;
    }


}
