package io.army.criteria;


import javax.annotation.Nullable;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link  SQLParam}</li>
 *          <li>SQL literal expression</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SqlValueParam extends TypeInfer {


    interface SingleValue extends SqlValueParam {


    }

    interface SingleAnonymousValue extends SingleValue {

        @Nullable
        Object value();
    }


    interface NonNullValue extends SqlValueParam {

    }

    interface MultiValue extends SqlValueParam, NonNullValue {

        int columnSize();

    }


    interface NamedValue extends SqlValueParam {

        String name();

    }

    interface NamedMultiValue extends NamedValue, MultiValue {

    }


}
