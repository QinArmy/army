package io.army.boot;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import org.springframework.aop.Pointcut;

import java.lang.reflect.Method;
import java.util.Map;

interface DomainSetterPointcut extends Pointcut {

    Map<Method, FieldMeta<?, ?>> setterFieldMap();

    TableMeta<?> tableMeta();
}
