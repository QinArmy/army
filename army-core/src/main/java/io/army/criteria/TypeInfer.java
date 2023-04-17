package io.army.criteria;

import io.army.meta.TypeMeta;

public interface TypeInfer extends Item {

    /**
     * @throws CriteriaException throw when this is {@link DelayTypeInfer} and {@link DelayTypeInfer#isDelay()} is false.
     */

    TypeMeta typeMeta();


    interface TypeUpdateSpec extends TypeInfer {

        TypeInfer mapTo(TypeMeta paramMeta);

    }

    interface DelayTypeInfer extends TypeInfer {

        boolean isDelay();

    }


}
