package io.army.dialect;

import io.army.criteria.CriteriaException;
import io.army.mapping.MappingType;
import io.army.util._StringUtils;

public abstract class _DialectUtils {

    protected _DialectUtils() {
        throw new UnsupportedOperationException();
    }



    public static String quoteIfNeed(MappingType mappingType, String textValue) {

        return "";
    }



    /*################################## blow package method ##################################*/



    public static String parentAlias(final String tableAlias) {
        return "p_of_" + tableAlias;
    }

    public static void validateTableAlias(final String tableAlias) {
        if (!_StringUtils.hasText(tableAlias)) {
            throw new CriteriaException("Alias of table or sub query must has text.");
        }
        if (tableAlias.startsWith(_Constant.FORBID_ALIAS)) {
            String m = String.format("Error,Alias[%s] of table or sub query start with %s."
                    , tableAlias, _Constant.FORBID_ALIAS);
            throw new CriteriaException(m);
        }
    }


    /*################################## blow private static innner class ##################################*/


}
