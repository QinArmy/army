package io.army.stmt;

import io.army.criteria.NamedElementParam;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link StrictParamValue}</li>
 *         <li>{@link io.army.criteria.NamedParam}</li>
 *         <li>{@link io.army.criteria.NonNullNamedParam}</li>
 *         <li>{@link NamedElementParam}</li>
 *     </ul>
 * </p>
 */
public interface ParamValue {

    ParamMeta paramMeta();

    @Nullable
    Object value();

    static StrictParamValue build(ParamMeta paramMeta, @Nullable Object value) {
        return new ParamValueImpl(paramMeta, value);
    }
}
