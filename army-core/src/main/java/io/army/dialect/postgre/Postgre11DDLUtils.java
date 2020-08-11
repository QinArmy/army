package io.army.dialect.postgre;

import io.army.dialect.DDLUtils;
import io.army.sqltype.PostgreDataType;
import io.army.util.StringUtils;

import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

abstract class Postgre11DDLUtils extends DDLUtils {

    private static final Pattern DOLLAR_QUOTING_PATTERN = Pattern.compile("(\\$.*\\$).*\\1");

    private static final Pattern NOT_EMPTY_NUMBER_PATTERN = Pattern.compile("(?:\\d*)?\\.?(?:\\d*)?(?:e[+-]?\\d+)?");

    /* static final Set< PostgreDataType> NUMBER_TYPE_SET = EnumSet.of(
             PostgreDataType.SMALLINT,
             PostgreDataType.INTEGER,
             PostgreDataType.BIGINT,
             PostgreDataType.DECIMAL,

             PostgreDataType.REAL,
             PostgreDataType.DOUBLE_PRECISION,
             PostgreDataType.MONEY
     );
 */
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


    static boolean binaryConstant(String expression) {
        return expression.length() > 3
                && Character.toUpperCase(expression.charAt(0)) == 'B'
                && expression.charAt(1) == '\''
                && expression.charAt(expression.length() - 1) == '\'';
    }

    static boolean dollarQuotingExpression(String defaultExp) {
        return DOLLAR_QUOTING_PATTERN.matcher(defaultExp).matches();
    }

    static boolean numberExpression(String expression) {
        return !StringUtils.isEmpty(expression)
                && NOT_EMPTY_NUMBER_PATTERN.matcher(expression).matches();
    }

    /*################################## blow private method ##################################*/


}
