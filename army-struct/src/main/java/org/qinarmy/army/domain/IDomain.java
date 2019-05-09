package org.qinarmy.army.domain;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * created  on 2018/11/20.
 */
public interface IDomain<D extends IDomain<D>> {


    String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

    /**
     * 1970-01-01 00:00:00 ,{@link System#currentTimeMillis() == 0}
     */
    // @see Column#zoneId()
    String SOURCE_TIMESTAMP = "SOURCE_TIMESTAMP";

    String SOURCE_DATE = "'1970-01-01'";

    String CURRENT_DATE = "CURRENT_DATE";

    String CURRENT_TIME = "CURRENT_TIME";

    String ZERO = "0";

    String ONE = "1";

    String EMPTY = "''";

    String DECIMAL_ZERO = "0.00";

    String N = "'N'";

    String Y = "'Y'";

    String UTF_8 = StandardCharsets.UTF_8.name();


    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    @Override
    String toString();


    Serializable getId();


    Boolean getVisible();

    D setVisible(Boolean visible);

    Integer getVersion();

    D setVersion(Integer version);

    LocalDateTime getCreateTime();

    D setCreateTime(LocalDateTime createTime);

    LocalDateTime getUpdateTime();

    D setUpdateTime(LocalDateTime updateTime);


    static boolean domainEquals(@NonNull IDomain domain, @Nullable Object obj) {
        if (domain == obj) {
            return true;
        }
        Class<?> clazz = domain.getClass();
        if (!(clazz.isInstance(obj))) return false;
        Method getIdMethod;
        try {
            getIdMethod = clazz.getMethod("getId");
            Object id = getIdMethod.invoke(domain);
            // o 已不为 null
            Object o_id = getIdMethod.invoke(obj);
            return id == null ? o_id == null : id.equals(o_id);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format(
                    "entity[%s] hasn't getId() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //get 不会抛异常,故不会到这里
            throw new RuntimeException(e);
        }
    }

}
