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

    String ZERO_YEAR = "$ZERO_YEAR$";

    String ZERO_DATE = "$ZERO_DATE$";

    String ZERO_DATE_TIME = "$ZERO_DATE_TIME$";

    String ZERO = "0";

    String ONE = "1";

    String DECIMAL_ZERO = "0.00";

    String N = "N";

    String Y = "Y";

    String UTF_8 = StandardCharsets.UTF_8.name();

    Boolean getVisible();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();



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
                    "entity[%s] hasn'table getId() method", clazz.getName()
            ));
        } catch (IllegalAccessException | InvocationTargetException e) {
            //get 不会抛异常,故不会到这里
            throw new RuntimeException(e);
        }
    }

}
