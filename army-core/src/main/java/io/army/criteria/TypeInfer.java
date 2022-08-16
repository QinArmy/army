package io.army.criteria;

import io.army.meta.ParamMeta;

public interface TypeInfer {

    ParamMeta paramMeta();


    interface DelayTypeInfer extends TypeInfer {

        boolean isPrepared();
    }

}
