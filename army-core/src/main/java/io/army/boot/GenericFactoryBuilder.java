package io.army.boot;

import io.army.codec.FieldCodec;
import io.army.env.Environment;

import java.util.Collection;

public interface GenericFactoryBuilder {


    GenericFactoryBuilder fieldCodecs(Collection<FieldCodec> fieldCodecs);

    GenericFactoryBuilder name(String sessionFactoryName);

    GenericFactoryBuilder environment(Environment environment);


}
