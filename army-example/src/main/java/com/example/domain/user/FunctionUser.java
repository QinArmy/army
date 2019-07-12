package com.example.domain.user;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

import static com.example.domain.user.User.FUNCTION_VALUE;

/**
 * created  on 2018/9/27.
 */
@Table(name = "u_function_user", comment = "功能用户表,这个表中的用户是为实现系统功能而存在,是不可以参与投资与借款的")
@DiscriminatorValue(FUNCTION_VALUE)
public class FunctionUser extends User {

    @Column(defaultValue = ZERO, comment = "功能用户的类型")
    private FunctionType functionType;

    public FunctionType getFunctionType() {
        return functionType;
    }

    public FunctionUser setFunctionType(FunctionType functionType) {
        this.functionType = functionType;
        return this;
    }
}
