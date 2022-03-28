package io.army.proxy;

import io.army.lang.Nullable;

public interface ArmyProxy {

    //TODO 定制 byte buddy 实现,修改方法名.
    void setArmy$_interceptor$$__(Object wrapper);

    //TODO 定制 byte buddy 实现,修改方法名.
    @Nullable
    Object getArmy$_interceptor$$__();

}
