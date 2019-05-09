package com.example.domain;


import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用于接收客户端参数的 Bean 的基类.
 * <p>
 * created  on 02/03/2018.
 */
public class Form implements Serializable {

    public static final String VIEW_PARAM_NAME = "viewType";

    private static final long serialVersionUID = 400080952500076366L;


    @Pattern(regexp = "json|html|text|xml|excel|pdf|word|file")
    private String viewType;

    /**
     * 定制属性
     */
    private String _custom;


    /**
     * 当数据由客户端传且不直接用于创建实体时使用此校验组
     */
    public interface Client {
    }

    /**
     * 创建业务(由客户端发起的创建请求)的校验分组
     */
    public interface Insert {
    }

    /**
     * 删除业务的校验分组
     */
    public interface Delete {
    }

    /**
     * 查询业务的校验分组
     */
    public interface Query {
    }

    /**
     * 更新业务的校验分组
     */
    public interface Update {

    }

    public interface Detail {
    }


    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String get_custom() {
        return _custom;
    }

    public Form set_custom(String _custom) {
        this._custom = _custom;
        return this;
    }


}
