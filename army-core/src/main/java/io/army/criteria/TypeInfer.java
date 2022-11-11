package io.army.criteria;

import io.army.meta.TypeMeta;

public interface TypeInfer extends Item {

    TypeMeta typeMeta();


    interface TypeUpdateSpec {

        Expression asType(TypeMeta paramMeta);

    }


}
