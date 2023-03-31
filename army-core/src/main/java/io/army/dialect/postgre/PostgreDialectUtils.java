package io.army.dialect.postgre;

import java.util.HashSet;
import java.util.Set;

abstract class PostgreDialectUtils {
    PostgreDialectUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a modifiable set
     * @see <a href="https://www.postgresql.org/docs/current/sql-keywords-appendix.html">SQL Key Words</a>
     */
    static Set<String> createKeywordsSet() {
        final Set<String> keywords = new HashSet<>((int) (282 / 0.75f));
        // below postgre  reserved key words,today 2023-03-31

        keywords.add("ABS");
        keywords.add("ACOS");
        keywords.add("ALL");
        keywords.add("ALLOCATE");
        keywords.add("ANALYSE");
        keywords.add("ANALYZE");
        keywords.add("AND");
        keywords.add("ANY");
        keywords.add("ARE");
        keywords.add("ARRAY");
        keywords.add("ARRAY_AGG");
        keywords.add("ARRAY_MAX_CARDINALITY");
        keywords.add("AS");
        keywords.add("ASIN");
        keywords.add("ASYMMETRIC");
        keywords.add("ATAN");
        keywords.add("AUTHORIZATION");
        keywords.add("AVG");
        keywords.add("BEGIN_FRAME");
        keywords.add("BEGIN_PARTITION");
        keywords.add("BINARY");
        keywords.add("BIT_LENGTH");
        keywords.add("BLOB");
        keywords.add("BOTH");
        keywords.add("CARDINALITY");
        keywords.add("CASE");
        keywords.add("CAST");
        keywords.add("CEIL");
        keywords.add("CEILING");
        keywords.add("CHARACTER_LENGTH");
        keywords.add("CHAR_LENGTH");
        keywords.add("CHECK");
        keywords.add("CLASSIFIER");
        keywords.add("CLOB");
        keywords.add("COLLATE");
        keywords.add("COLLECT");
        keywords.add("COLUMN");
        keywords.add("CONCURRENTLY");
        keywords.add("CONDITION");
        keywords.add("CONNECT");
        keywords.add("CONSTRAINT");
        keywords.add("CONTAINS");
        keywords.add("CONVERT");
        keywords.add("CORR");
        keywords.add("CORRESPONDING");
        keywords.add("COS");
        keywords.add("COSH");
        keywords.add("COUNT");
        keywords.add("COVAR_POP");
        keywords.add("COVAR_SAMP");
        keywords.add("CREATE");
        keywords.add("CROSS");
        keywords.add("CUME_DIST");
        keywords.add("CURRENT_CATALOG");
        keywords.add("CURRENT_DATE");
        keywords.add("CURRENT_DEFAULT_TRANSFORM_GROUP");
        keywords.add("CURRENT_PATH");
        keywords.add("CURRENT_ROLE");
        keywords.add("CURRENT_ROW");
        keywords.add("CURRENT_SCHEMA");
        keywords.add("CURRENT_TIME");
        keywords.add("CURRENT_TIMESTAMP");
        keywords.add("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
        keywords.add("CURRENT_USER");
        keywords.add("DATALINK");
        keywords.add("DATE");
        keywords.add("DECFLOAT");
        keywords.add("DEFAULT");
        keywords.add("DEFINE");
        keywords.add("DENSE_RANK");
        keywords.add("DEREF");
        keywords.add("DESCRIBE");
        keywords.add("DETERMINISTIC");
        keywords.add("DISCONNECT");
        keywords.add("DISTINCT");
        keywords.add("DLNEWCOPY");
        keywords.add("DLPREVIOUSCOPY");
        keywords.add("DLURLCOMPLETE");
        keywords.add("DLURLCOMPLETEONLY");
        keywords.add("DLURLCOMPLETEWRITE");
        keywords.add("DLURLPATH");
        keywords.add("DLURLPATHONLY");
        keywords.add("DLURLPATHWRITE");
        keywords.add("DLURLSCHEME");
        keywords.add("DLURLSERVER");
        keywords.add("DLVALUE");
        keywords.add("DO");
        keywords.add("DYNAMIC");
        keywords.add("ELEMENT");
        keywords.add("ELSE");
        keywords.add("END");
        keywords.add("END-EXEC");
        keywords.add("END_FRAME");
        keywords.add("END_PARTITION");
        keywords.add("EQUALS");
        keywords.add("EVERY");
        keywords.add("EXCEPT");
        keywords.add("EXCEPTION");
        keywords.add("EXEC");
        keywords.add("EXP");
        keywords.add("FALSE");
        keywords.add("FETCH");
        keywords.add("FIRST_VALUE");
        keywords.add("FLOOR");
        keywords.add("FOR");
        keywords.add("FOREIGN");
        keywords.add("FRAME_ROW");
        keywords.add("FREE");
        keywords.add("FREEZE");
        keywords.add("FROM");
        keywords.add("FULL");
        keywords.add("FUSION");
        keywords.add("GET");
        keywords.add("GRANT");
        keywords.add("GROUP");
        keywords.add("HAVING");
        keywords.add("ILIKE");
        keywords.add("IN");
        keywords.add("INDICATOR");
        keywords.add("INITIAL");
        keywords.add("INNER");
        keywords.add("INTERSECT");
        keywords.add("INTERSECTION");
        keywords.add("INTO");
        keywords.add("IS");
        keywords.add("ISNULL");
        keywords.add("JOIN");
        keywords.add("JSON_ARRAY");
        keywords.add("JSON_ARRAYAGG");
        keywords.add("JSON_EXISTS");
        keywords.add("JSON_OBJECT");
        keywords.add("JSON_OBJECTAGG");
        keywords.add("JSON_QUERY");
        keywords.add("JSON_TABLE");
        keywords.add("JSON_TABLE_PRIMITIVE");
        keywords.add("JSON_VALUE");
        keywords.add("LAG");
        keywords.add("LAST_VALUE");
        keywords.add("LATERAL");
        keywords.add("LEAD");
        keywords.add("LEADING");
        keywords.add("LEFT");
        keywords.add("LIKE");
        keywords.add("LIKE_REGEX");
        keywords.add("LISTAGG");
        keywords.add("LN");
        keywords.add("LOCALTIME");
        keywords.add("LOCALTIMESTAMP");
        keywords.add("LOG");
        keywords.add("LOG10");
        keywords.add("LOWER");
        keywords.add("MATCHES");
        keywords.add("MATCH_NUMBER");
        keywords.add("MATCH_RECOGNIZE");
        keywords.add("MAX");
        keywords.add("MEASURES");
        keywords.add("MEMBER");
        keywords.add("MIN");
        keywords.add("MOD");
        keywords.add("MODIFIES");
        keywords.add("MODULE");
        keywords.add("MULTISET");
        keywords.add("NATURAL");
        keywords.add("NCLOB");
        keywords.add("NOT");
        keywords.add("NOTNULL");
        keywords.add("NTH_VALUE");
        keywords.add("NTILE");
        keywords.add("NULL");
        keywords.add("OCCURRENCES_REGEX");
        keywords.add("OCTET_LENGTH");
        keywords.add("OFFSET");
        keywords.add("OMIT");
        keywords.add("ON");
        keywords.add("ONE");
        keywords.add("ONLY");
        keywords.add("OPEN");
        keywords.add("OR");
        keywords.add("ORDER");
        keywords.add("OUTER");
        keywords.add("OVERLAPS");
        keywords.add("PATTERN");
        keywords.add("PER");
        keywords.add("PERCENT");
        keywords.add("PERCENTILE_CONT");
        keywords.add("PERCENTILE_DISC");
        keywords.add("PERCENT_RANK");
        keywords.add("PERIOD");
        keywords.add("PERMUTE");
        keywords.add("PORTION");
        keywords.add("POSITION_REGEX");
        keywords.add("POWER");
        keywords.add("PRECEDES");
        keywords.add("PRIMARY");
        keywords.add("PTF");
        keywords.add("RANK");
        keywords.add("READS");
        keywords.add("REFERENCES");
        keywords.add("REGR_AVGX");
        keywords.add("REGR_AVGY");
        keywords.add("REGR_COUNT");
        keywords.add("REGR_INTERCEPT");
        keywords.add("REGR_R2");
        keywords.add("REGR_SLOPE");
        keywords.add("REGR_SXX");
        keywords.add("REGR_SXY");
        keywords.add("REGR_SYY");
        keywords.add("RESULT");
        keywords.add("RIGHT");
        keywords.add("ROW_NUMBER");
        keywords.add("RUNNING");
        keywords.add("SCOPE");
        keywords.add("SEEK");
        keywords.add("SELECT");
        keywords.add("SENSITIVE");
        keywords.add("SESSION_USER");
        keywords.add("SIMILAR");
        keywords.add("SIN");
        keywords.add("SINH");
        keywords.add("SOME");
        keywords.add("SPECIFIC");
        keywords.add("SPECIFICTYPE");
        keywords.add("SQLCODE");
        keywords.add("SQLERROR");
        keywords.add("SQLEXCEPTION");
        keywords.add("SQLSTATE");
        keywords.add("SQLWARNING");
        keywords.add("SQRT");
        keywords.add("STATIC");
        keywords.add("STDDEV_POP");
        keywords.add("STDDEV_SAMP");
        keywords.add("SUBMULTISET");
        keywords.add("SUBSET");
        keywords.add("SUBSTRING_REGEX");
        keywords.add("SUCCEEDS");
        keywords.add("SUM");
        keywords.add("SYMMETRIC");
        keywords.add("SYSTEM_TIME");
        keywords.add("SYSTEM_USER");
        keywords.add("TABLE");
        keywords.add("TABLESAMPLE");
        keywords.add("TAN");
        keywords.add("TANH");
        keywords.add("THEN");
        keywords.add("TIMEZONE_HOUR");
        keywords.add("TIMEZONE_MINUTE");
        keywords.add("TO");
        keywords.add("TRAILING");
        keywords.add("TRANSLATE");
        keywords.add("TRANSLATE_REGEX");
        keywords.add("TRANSLATION");
        keywords.add("TRIM_ARRAY");
        keywords.add("TRUE");
        keywords.add("UNION");
        keywords.add("UNIQUE");
        keywords.add("UNMATCHED");
        keywords.add("UNNEST");
        keywords.add("UPPER");
        keywords.add("USER");
        keywords.add("USING");
        keywords.add("VALUE_OF");
        keywords.add("VARBINARY");
        keywords.add("VARIADIC");
        keywords.add("VAR_POP");
        keywords.add("VAR_SAMP");
        keywords.add("VERBOSE");
        keywords.add("VERSIONING");
        keywords.add("WHEN");
        keywords.add("WHENEVER");
        keywords.add("WHERE");
        keywords.add("WIDTH_BUCKET");
        keywords.add("WINDOW");
        keywords.add("WITH");
        keywords.add("XMLAGG");
        keywords.add("XMLBINARY");
        keywords.add("XMLCAST");
        keywords.add("XMLCOMMENT");
        keywords.add("XMLDOCUMENT");
        keywords.add("XMLITERATE");
        keywords.add("XMLQUERY");
        keywords.add("XMLTEXT");
        keywords.add("XMLVALIDATE");

        return keywords;
    }
}
