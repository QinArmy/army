package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.criteria.SelectPart;
import io.army.criteria.Selection;
import io.army.criteria.postgre.PostgreFuncColExp;
import io.army.criteria.postgre.PostgreFuncTable;
import io.army.criteria.postgre.PostgreRowsFromTable;
import io.army.dialect.SQL;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;

import java.util.*;

final class PostgreRowsFromTableImpl implements PostgreRowsFromTable {

    static PostgreRowsFromTable build(List<PostgreFuncTable> tableList, final @Nullable MappingType withOrdinalityType
            , final String tableAlias
            , List<String> aliasList) {

        if (withOrdinalityType != null
                && withOrdinalityType.javaType() != Integer.class
                && withOrdinalityType.javaType() != Long.class) {
            throw new IllegalArgumentException("withOrdinalityType's java type only is Integer/Long");
        }

        List<SelectPart> selectPartList = new ArrayList<>();
        final int aliasSize = aliasList.size();
        int index = 0;
        Map<String, Selection> selectionMap = new HashMap<>();
        Selection selection;
        String alias;

        for (PostgreFuncTable table : tableList) {
            for (PostgreFuncColExp<?> colExp : table.columnExprList()) {
                if (aliasSize == 0) {
                    alias = colExp.columnName();
                } else if (index < aliasSize) {
                    alias = aliasList.get(index);
                } else {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "function[%s] column alias size error."
                            , tableAlias);
                }

                selection = colExp.as(alias);
                selectPartList.add(selection);
                if (selectionMap.putIfAbsent(alias, selection) != selection) {
                    throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                            , "function derived column name[%s] duplication.");
                }
                index++;
            }
        }
        if (withOrdinalityType != null) {
            String ordinalityAlias;
            if (aliasSize == 0) {
                ordinalityAlias = "ordinality";
            } else if (index < aliasSize) {
                ordinalityAlias = aliasList.get(index);
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "function[%s] ORDINALITY has no alias."
                        , tableAlias);
            }
            selectPartList.add(new WithOrdinalitySelection(ordinalityAlias, withOrdinalityType));
        }

        return new PostgreRowsFromTableImpl(tableList, withOrdinalityType, tableAlias, selectPartList, selectionMap);

    }

    private final List<PostgreFuncTable> tableList;

    private final MappingType withOrdinalityType;

    private final String tableAlias;

    private final List<SelectPart> selectPartList;

    private final Map<String, Selection> selectionMap;


    private PostgreRowsFromTableImpl(List<PostgreFuncTable> tableList, @Nullable MappingType withOrdinalityType, String tableAlias
            , List<SelectPart> selectPartList, Map<String, Selection> selectionMap) {
        this.tableList = Collections.unmodifiableList(tableList);
        this.withOrdinalityType = withOrdinalityType;
        this.tableAlias = tableAlias;
        this.selectPartList = Collections.unmodifiableList(selectPartList);
        this.selectionMap = Collections.unmodifiableMap(selectionMap);
    }


    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public List<SelectPart> selectPartList() {
        return this.selectPartList;
    }

    @Override
    public Selection selection(String derivedFieldName) {
        Selection selection = this.selectionMap.get(derivedFieldName);
        if (selection == null) {
            throw new IllegalArgumentException(String.format("not found selection[%s]", derivedFieldName));
        }
        return selection;
    }

    @Override
    public void appendSQL(SQLContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append(" ROWS FROM ( ");
        for (Iterator<PostgreFuncTable> iterator = this.tableList.iterator(); iterator.hasNext(); ) {
            iterator.next().appendSQL(context);
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");

        if (this.withOrdinalityType != null) {
            builder.append(" WITH ORDINALITY");
        }

        SQL sql = context.dql();
        builder.append(" AS ")
                .append(sql.quoteIfNeed(this.tableAlias))
                .append(" ( ")
        ;

        for (Iterator<SelectPart> iterator = selectPartList.iterator(); iterator.hasNext(); ) {
            SelectPart selectPart = iterator.next();
            if (selectPart instanceof Selection) {
                builder.append(sql.quoteIfNeed(((Selection) selectPart).alias()));
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "PostgreRowsFromTableImpl column[%s] error."
                        , selectPart);
            }
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");
    }


    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder()
                .append(" ROWS FROM ( ");

        for (Iterator<PostgreFuncTable> iterator = this.tableList.iterator(); iterator.hasNext(); ) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");

        if (this.withOrdinalityType != null) {
            builder.append(" WITH ORDINALITY");
        }

        builder.append(" AS ")
                .append(this.tableAlias)
                .append(" ( ")
        ;

        for (Iterator<SelectPart> iterator = selectPartList.iterator(); iterator.hasNext(); ) {
            SelectPart selectPart = iterator.next();
            if (selectPart instanceof Selection) {
                builder.append(((Selection) selectPart).alias());
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "PostgreRowsFromTableImpl column[%s] error."
                        , selectPart);
            }
            if (iterator.hasNext()) {
                builder.append(",");
            }
        }
        builder.append(" )");

        return builder.toString();
    }


    private static final class WithOrdinalitySelection implements Selection {

        private final String alias;

        private final MappingType mappingType;

        private WithOrdinalitySelection(String alias, MappingType mappingType) {
            this.alias = alias;
            this.mappingType = mappingType;
        }

        @Override
        public String alias() {
            return this.alias;
        }

        @Override
        public MappingType mappingType() {
            return this.mappingType;
        }

        @Override
        public void appendSQL(SQLContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void appendSortPart(SQLContext context) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return String.format("ORDINALITY selection[%s],MappingType[%s] ", alias, mappingType);
        }
    }


}
