package io.army.dialect;

public enum Clause {

    SELECT(Keywords.SELECT),
    SELECT_LIST(""),
    INSERT_INTO(Keywords.INSERT_INTO),
    VALUE(Keywords.VALUE),
    VALUES(Keywords.VALUES),
    FROM(Keywords.FROM),
    ON(Keywords.ON),
    WHERE(Keywords.WHERE),
    GROUP_BY(Keywords.GROUP_BY),
    HAVING(Keywords.HAVING),
    ORDER_BY(Keywords.ORDER_BY),
    SUB_QUERY("");

    private final String keywords;

    Clause(String keywords) {
        this.keywords = keywords;
    }

    public void appendSQL(StringBuilder builder) {
        if (!"".equals(keywords)) {
            builder.append(" ")
                    .append(keywords);
        }
    }
}
