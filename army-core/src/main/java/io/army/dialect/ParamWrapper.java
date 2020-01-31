package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;

public interface ParamWrapper {

    MappingType mappingType();

    @Nullable
    Object value();

    static ParamWrapper build(MappingType mappingType,@Nullable Object value){
        return new ParamWrapperImpl(mappingType,value);
    }
}
