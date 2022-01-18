package io.army.cache;

import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.util.ReflectionUtils;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class SetterMethodMatcherPointcut extends StaticMethodMatcherPointcut
        implements ClassFilter, DomainSetterPointcut {

    static SetterMethodMatcherPointcut build(TableMeta<?> tableMeta) {
        Map<Method, FieldMeta<?, ?>> setterFieldMap = new HashMap<>();

        if (tableMeta instanceof ChildTableMeta) {
            ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
            for (FieldMeta<?, ?> fieldMeta : childMeta.parentMeta().fields()) {
                setterFieldMap.put(ReflectionUtils.findSetterMethod(fieldMeta), fieldMeta);
            }
        }

        final FieldMeta<?, ?> idMeta = tableMeta.id();
        for (FieldMeta<?, ?> fieldMeta : tableMeta.fields()) {
            Method method = ReflectionUtils.findSetterMethod(fieldMeta);
            if (fieldMeta == idMeta) {
                setterFieldMap.putIfAbsent(method, fieldMeta);
            } else if (setterFieldMap.put(method, fieldMeta) != null) {
                throw new MetaException("FieldMeta[%s] duplication.", fieldMeta);
            }
        }

        return new SetterMethodMatcherPointcut(tableMeta, setterFieldMap);
    }

    private final TableMeta<?> tableMeta;

    private final Map<Method, FieldMeta<?, ?>> setterFieldMap;

    SetterMethodMatcherPointcut(TableMeta<?> tableMeta, Map<Method, FieldMeta<?, ?>> setterFieldMap) {
        this.tableMeta = tableMeta;
        this.setterFieldMap = Collections.unmodifiableMap(setterFieldMap);
    }

    @Override
    public Map<Method, FieldMeta<?, ?>> setterFieldMap() {
        return this.setterFieldMap;
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.tableMeta;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        return clazz == this.tableMeta.javaType();
    }

    @Override
    public final ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return this.tableMeta.javaType() == targetClass
                && this.setterFieldMap.containsKey(method);
    }


}
