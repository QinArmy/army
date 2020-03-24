package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.*;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.util.*;

class PostgreFuncTableImpl implements PostgreFuncTable {

    static PostgreFuncTable build(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList) {
        return new PostgreFuncTableImpl(funcExp, colDefExpList);
    }

    static PostgreAliasFuncTable build(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList, String tableAlias) {
        return new AliasPostgreFuncTable(funcExp, colDefExpList, tableAlias);
    }

    private final Expression<?> funcExp;

    private final List<PostgreFuncColExp<?>> colDefExpList;

    private final Map<String, Selection> selectionMap;

    private List<SelectPart> selectPartList;

    private PostgreFuncTableImpl(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList) {
        this.funcExp = funcExp;
        this.colDefExpList = Collections.unmodifiableList(colDefExpList);

        Map<String, Selection> map = new HashMap<>();
        for (PostgreFuncColExp<?> colExp : colDefExpList) {
            if (map.putIfAbsent(colExp.alias(), colExp) != null) {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "function table column definition[%s] duplication."
                        , colExp.alias());
            }
        }
        this.selectionMap = Collections.unmodifiableMap(map);
    }

    @Override
    public final List<PostgreFuncColExp<?>> columnExprList() {
        return this.colDefExpList;
    }

    @Override
    public List<SelectPart> selectPartList() {
        if (this.selectPartList == null) {
            this.selectPartList = Collections.unmodifiableList(new ArrayList<>(colDefExpList));
        }
        return this.selectPartList;
    }

    @Override
    public Selection selection(String derivedFieldName) {
        Selection selection = this.selectionMap.get(derivedFieldName);
        Assert.notNull(selection, "derivedFieldName[%s] has no selection.");
        return selection;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        funcExp.appendSQL(context);
        StringBuilder builder = context.stringBuilder();


        String tableAlias = tableAlias();
        if (StringUtils.hasText(tableAlias)) {
            builder.append(" AS ")
                    .append(context.dml().quoteIfNeed(tableAlias));
        }
        if (colDefExpList.isEmpty()) {
            return;
        }
        if (!StringUtils.hasText(tableAlias)) {
            builder.append(" AS");
        }
        builder.append(" ( ");
        for (Iterator<PostgreFuncColExp<?>> iterator = this.colDefExpList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");
    }

    String tableAlias() {
        return "";
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(funcExp.toString());

        String tableAlias = tableAlias();
        if (StringUtils.hasText(tableAlias)) {
            builder.append(" AS ")
                    .append(tableAlias);
        }
        if (this.colDefExpList.isEmpty()) {
            return builder.toString();
        }
        if (!StringUtils.hasText(tableAlias)) {
            builder.append(" AS");
        }
        builder.append(" ( ");
        for (Iterator<PostgreFuncColExp<?>> iterator = this.colDefExpList.iterator(); iterator.hasNext(); ) {
            builder.append(" ")
                    .append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");
        return builder.toString();
    }

    private static final class AliasPostgreFuncTable extends PostgreFuncTableImpl implements PostgreAliasFuncTable {

        private final String tableAlias;

        AliasPostgreFuncTable(Expression<?> funcExp, List<PostgreFuncColExp<?>> colDefExpList, String tableAlias) {
            super(funcExp, colDefExpList);
            this.tableAlias = tableAlias;
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }
    }
}
