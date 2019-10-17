package io.army.domain;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * created  on 2018/11/20.
 */
public interface IDomain {


    String NOW = "NOW()";

    String SOURCE_DATE = "SOURCE_DATE";

    String MIDNIGHT = "MIDNIGHT";

    String SOURCE_DATE_TIME = "SOURCE_DATE_TIME";

    String CURRENT_DATE = "CURRENT_DATE()";

    String CURRENT_TIME = "CURRENT_TIME()";

    String ZERO = "0";

    String ONE = "1";

    String EMPTY = "''";

    String DECIMAL_ZERO = "0.00";

    String N = "N";

    String Y = "Y";

    String UTF_8 = StandardCharsets.UTF_8.name();


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
