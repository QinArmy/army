package io.army.criteria;

import io.army.meta.ParamMeta;

public interface TypeInfer {

    ParamMeta paramMeta();


    interface TypeUpdateSpec {

        SelectionSpec asType(ParamMeta paramMeta);

    }


}
