package io.army.criteria.impl;

import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;

class ArmySortItems implements ArmySortItem {

    static ArmySortItem create(final ArmyExpression exp, final Statement.AscDesc ascDesc,
                               final @Nullable Statement.NullsFirstLast firstLast) {
        if (ascDesc != SQLs.DESC && ascDesc != SQLs.ASC) {
            throw CriteriaUtils.unknownWords(ascDesc);
        } else if (firstLast != null && firstLast != SQLs.NULLS_LAST && firstLast != SQLs.NULLS_FIRST) {
            throw CriteriaUtils.unknownWords(firstLast);
        }
        final ArmySortItem sortItem;
        if (firstLast == null) {
            sortItem = new ArmySortItems(exp, ascDesc);
        } else {
            sortItem = new SortItemWithNullsOption(exp, ascDesc, firstLast);
        }
        return sortItem;
    }


    private final ArmyExpression sortItem;

    private final Statement.AscDesc ascDesc;

    private ArmySortItems(ArmyExpression sortItem, Statement.AscDesc ascDesc) {
        this.sortItem = sortItem;
        this.ascDesc = ascDesc;
    }

    @Override
    public final SortItem asSortItem() {
        //always this
        return this;
    }

    @Override
    public void appendSql(final _SqlContext context) {
        this.sortItem.appendSql(context);

        final StringBuilder sqlBuilder;
        sqlBuilder = context.sqlBuilder()
                .append(this.ascDesc.spaceRender());

        if (this instanceof SortItemWithNullsOption) {
            sqlBuilder.append(((SortItemWithNullsOption) this).nullOption);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(this.sortItem)
                .append(this.ascDesc.spaceRender());

        if (this instanceof SortItemWithNullsOption) {
            builder.append(((SortItemWithNullsOption) this).nullOption.spaceRender());
        }
        return builder.toString();
    }


    private static final class SortItemWithNullsOption extends ArmySortItems {

        private final Statement.NullsFirstLast nullOption;

        private SortItemWithNullsOption(ArmyExpression sortItem, Statement.AscDesc aesWord,
                                        Statement.NullsFirstLast nullOption) {
            super(sortItem, aesWord);
            this.nullOption = nullOption;
        }

    }//SortItemWithNullsOption

}
