package io.army.proxy;

import io.army.criteria.Update;
import io.army.criteria.UpdateStatement;

public interface _CacheBlock {

     Object id();

    Update statement();

     void success();

}
