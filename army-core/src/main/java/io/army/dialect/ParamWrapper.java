package io.army.dialect;

import io.army.meta.mapping.MappingType;
import org.springframework.lang.Nullable;

public interface ParamWrapper {

    MappingType mappingType();

    @Nullable
    Object value();

    static ParamWrapper build(MappingType mappingType,Object value){
        return new ParamWrapperImpl(mappingType,value);
    }
}
