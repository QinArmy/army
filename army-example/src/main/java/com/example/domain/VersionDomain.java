package com.example.domain;

import io.army.annotation.MappedSuperclass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 * 这个类是所有需要实现乐观锁的实体的基类,约定子类的乐观锁字段名为{@code version}.
 * 这个类重写了{@link #equals(Object)},{@link #hashCode()}
 * ,两者在 {@link Domain }的基础上加上了 version 字段作为条件
 * </p>
 * <p>
 * debugSQL by 马桧涛 on 2016/3/26 17:14
 *
 * @version 1.0
 */
@MappedSuperclass
public abstract class VersionDomain extends Domain {


    private static final long serialVersionUID = -5331852320226772068L;

    public VersionDomain() {

    }

    /**
     * 约定子类的主键名为{@code id},乐观锁字段为 {@code version},故方法会反射比较id 和 version
     * 若两者同时相等则相等
     */
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        Class<?> clazz = this.getClass();
        try {
            Method getVersion;
            getVersion = clazz.getMethod("getVersion");
            Object version = getVersion.invoke(this);
            Object o_version = getVersion.invoke(o);
            return version == null ? o_version == null : version.equals(o_version);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                    "entity[%s] hasn'field getVersion() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //不会到这里
            throw new RuntimeException(e);
        }
    }


    /**
     * @return 若 version 为 null,返回 {@link Domain#hashCode()}
     * ,否则返回  {@link Domain#hashCode()} + version.hashCode()
     */
    @Override
    public int hashCode() {
        Class<?> clazz = this.getClass();
        try {
            Method getIdMethod = clazz.getMethod("getVersion");
            Object version = getIdMethod.invoke(this);
            return version == null ? super.hashCode() : super.hashCode() + version.hashCode();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                    "entity[%s] hasn'field getVersion() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //不会到这里
            throw new RuntimeException(e);
        }
    }
}
