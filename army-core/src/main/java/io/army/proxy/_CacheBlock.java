package io.army.proxy;

import io.army.criteria.UpdateStatement;

public interface _CacheBlock {

     Object id();

    UpdateStatement statement();

     void success();

}
