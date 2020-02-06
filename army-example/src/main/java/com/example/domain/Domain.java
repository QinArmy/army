package com.example.domain;

import io.army.annotation.Column;
import io.army.annotation.MappedSuperclass;
import io.army.domain.IDomain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * <p>
 * 这个类是所有实体类的基类,约定所有实体类的主键名必须是 {@code id}.
 * 这个类重写了 {@link #equals(Object)},{@link #hashCode()},{@link #toString()}
 *
 * </p>
 *
 * @version 1.0
 */
@MappedSuperclass
public abstract class Domain extends BaseCriteria implements IDomain {

    public static final String ENUM_TYPE = "org.qinarmy.tasty.core.data.orm.CodeEnumUserType";

    private static final long serialVersionUID = 566097736173284352L;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;

    @Column
    private Integer version;


    public Domain() {

    }


    /**
     * 约定子类的主键名为{@code id},故方法会反射比较id
     * 若 调用 equals(Object) 方法的实例的 id 为 null ,则 返回 o.id == null,
     * 否则 返回 this.id.equals(o.id)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Class<?> clazz = this.getClass();
        if (!(clazz.isInstance(o))) return false;
        Method getIdMethod;
        try {
            getIdMethod = clazz.getMethod("getId");
            Object id = getIdMethod.invoke(this);
            // o 已不为 null
            Object o_id = getIdMethod.invoke(o);
            return id == null ? o_id == null : id.equals(o_id);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                    "entity[%s] hasn'table getId() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //get 不会抛异常,故不会到这里
            throw new RuntimeException(e);
        }
    }


    /**
     * @return 主键 {@code id} 的 hashCode * 31,若主键 null 则返回 0
     */
    @Override
    public int hashCode() {
        Class<?> clazz = this.getClass();
        try {
            Method getIdMethod = clazz.getMethod("getId");
            Object id = getIdMethod.invoke(this);
            return id == null ? 0 : 31 * id.hashCode();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                    "entity[%s] not getId() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //get 不会抛异常,故不会到这里
            throw new RuntimeException(e);
        }
    }


    public static String getEnumType() {
        return ENUM_TYPE;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Domain setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Domain setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public Domain setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public Domain setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
