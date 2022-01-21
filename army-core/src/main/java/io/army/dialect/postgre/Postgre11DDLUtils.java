package io.army.dialect.postgre;

import io.army.dialect.DDLUtils;
import io.army.sqltype.PostgreType;

import java.util.EnumSet;
import java.util.Set;

abstract class Postgre11DDLUtils extends DDLUtils {


    private static final Set<PostgreType> NEED_QUOTE_TYPE_SET = EnumSet.of(
            PostgreType.CHAR,
            PostgreType.VARCHAR,
            PostgreType.TEXT,
            PostgreType.INTERVAL,
            PostgreType.POINT,
            PostgreType.LINE,
            // PostgreDataType.LSEG,
            PostgreType.BOX,
            PostgreType.PATH,
            PostgreType.POLYGON,
            PostgreType.BYTEA

    );


    static boolean needQuoteForDefault(PostgreType dataType) {
        return NEED_QUOTE_TYPE_SET.contains(dataType);
    }




    /*################################## blow private method ##################################*/


}
