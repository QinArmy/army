package io.army.criteria;


import io.army.lang.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link  SqlParam}</li>
 *          <li>SQL literal expression</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SqlValueParam extends TypeInfer {


    interface SingleValue extends SqlValueParam {


    }

    interface SingleNonNamedValue extends SingleValue {

        @Nullable
        Object value();
    }


    interface NonNullValue extends SqlValueParam {

    }

    interface MultiValue extends SqlValueParam, NonNullValue {

        int valueSize();

    }


    interface NamedValue extends SqlValueParam {

        String name();

    }

    interface NamedMultiValue extends NamedValue, MultiValue {

    }


}
