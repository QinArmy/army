package io.army.example.finance.domain;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.example.Domain;
import io.army.example.struct.UserType;

import java.time.LocalDateTime;

@Table(name = "u_user", comment = "user")
@Inheritance("user_type")
public class User extends Domain {

    @Column
    private Long id;

    @Column
    private UserType userType;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column(precision = 64, comment = "user nick name")
    private String nickName;


    @Override
    public Long getId() {
        return this.id;
    }

    public UserType getUserType() {
        return this.userType;
    }

    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    public String getNickName() {
        return this.nickName;
    }


}
