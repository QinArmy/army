package io.army.proxy;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public interface BuddyInterceptor {

    @RuntimeType
    Object intercept(@This Object instance, @Origin Method method, @SuperCall Callable<?> callable)
            throws Throwable;


}
