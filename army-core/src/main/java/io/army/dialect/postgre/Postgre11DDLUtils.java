package io.army.dialect.postgre;

import io.army.dialect.DDLUtils;
import io.army.sqltype.PostgreDataType;

import java.util.EnumSet;
import java.util.Set;

abstract class Postgre11DDLUtils extends DDLUtils {


    private static final Set<PostgreDataType> NEED_QUOTE_TYPE_SET = EnumSet.of(
            PostgreDataType.CHAR,
            PostgreDataType.VARCHAR,
            PostgreDataType.TEXT,
            PostgreDataType.INTERVAL,
            PostgreDataType.POINT,
            PostgreDataType.LINE,
            PostgreDataType.LSEG,
            PostgreDataType.BOX,
            PostgreDataType.PATH,
            PostgreDataType.POLYGON,
            PostgreDataType.BYTEA

    );


    static boolean needQuoteForDefault(PostgreDataType dataType) {
        return NEED_QUOTE_TYPE_SET.contains(dataType);
    }




    /*################################## blow private method ##################################*/


}
