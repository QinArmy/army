package io.army.dialect;

public enum Clause {

    SELECT(Constant.SELECT),
    SELECT_LIST(""),
    INSERT_INTO(Constant.INSERT_INTO),
    VALUE(Constant.VALUE),
    VALUES(Constant.VALUES),
    FROM(Constant.FROM),
    ON(Constant.ON),
    WHERE(Constant.WHERE),
    GROUP_BY(Constant.GROUP_BY),
    HAVING(Constant.HAVING),
    ORDER_BY(Constant.ORDER_BY),
    SUB_QUERY(""),
    PART_START(""),
    PART_END("");

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
