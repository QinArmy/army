package com.example.fortune.domain.user;

import com.example.fortune.domain.VersionDomain;
import io.army.annotation.*;

@Table(name = "u_user", comment = "user"
        , indexes = {@Index(name = "uni_user_group_no_user_no", columnList = {"user_group_no", "user_no"}, unique = true)
}
)
@Inheritance("user_type")
@SuppressWarnings("unchecked")
public class User<T extends User<T>> extends VersionDomain {

    @Column
    private UserType userType;

    @Column(precision = 64, comment = "nick name of user")
    private String nickName;

    @Column(updatable = false, comment = "primary key of cer_certificate")
    private Long certificateId;

    @Column(updatable = false, precision = UserConstant.USER_GROUP_NO_LENGTH, comment = "number of user group,unique with user_no")
    private String userGroupNo;

    @Column(updatable = false, precision = UserConstant.USER_NO_LENGTH, comment = "number of user,unique with user_group_no")
    private String userNo;

    @Codec
    @Column(precision = 44, comment = "cipher text of user password")
    private String password;


    public UserType getUserType() {
        return userType;
    }

    public T setUserType(UserType userType) {
        this.userType = userType;
        return (T) this;
    }

    public String getNickName() {
        return nickName;
    }

    public T setNickName(String nickName) {
        this.nickName = nickName;
        return (T) this;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public T setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
        return (T) this;
    }

    public String getUserGroupNo() {
        return userGroupNo;
    }

    public T setUserGroupNo(String userGroupNo) {
        this.userGroupNo = userGroupNo;
        return (T) this;
    }

    public String getUserNo() {
        return userNo;
    }

    public T setUserNo(String userNo) {
        this.userNo = userNo;
        return (T) this;
    }

    public String getPassword() {
        return password;
    }

    public T setPassword(String password) {
        this.password = password;
        return (T) this;
    }
}
