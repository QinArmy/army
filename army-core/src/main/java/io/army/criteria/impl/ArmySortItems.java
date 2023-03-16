package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SortItem;
import io.army.criteria.Statement;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;

class ArmySortItems implements ArmySortItem {

    static ArmySortItem create(final Expression exp, final Statement.AscDesc ascDesc) {
        if (!(ascDesc instanceof SQLs.KeyWordAscDesc)) {
            throw CriteriaUtils.unknownWords(ascDesc);
        }
        return new ArmySortItems((ArmyExpression) exp, (SQLs.KeyWordAscDesc) ascDesc);
    }

    static ArmySortItem create(final Expression exp, final Statement.NullsFirstLast nullOption) {
        if (!(nullOption instanceof SQLs.KeyWordsNullsFirstLast)) {
            throw CriteriaUtils.unknownWords(nullOption);
        }
        return new SortItemWithNullsOption((ArmyExpression) exp
                , null
                , (SQLs.KeyWordsNullsFirstLast) nullOption);
    }

    static ArmySortItem create(final Expression exp, final Statement.AscDesc ascDesc
            , final Statement.NullsFirstLast nullOption) {
        if (!(ascDesc instanceof SQLs.KeyWordAscDesc)) {
            throw CriteriaUtils.unknownWords(ascDesc);
        } else if (!(nullOption instanceof SQLs.KeyWordsNullsFirstLast)) {
            throw CriteriaUtils.unknownWords(nullOption);
        }
        return new SortItemWithNullsOption((ArmyExpression) exp
                , (SQLs.KeyWordAscDesc) ascDesc
                , (SQLs.KeyWordsNullsFirstLast) nullOption);
    }


    private final ArmyExpression sortItem;

    private final SQLsSyntax.KeyWordAscDesc aseWord;

    private ArmySortItems(ArmyExpression sortItem, @Nullable SQLsSyntax.KeyWordAscDesc aseWord) {
        this.sortItem = sortItem;
        this.aseWord = aseWord;
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
        sqlBuilder = context.sqlBuilder();

        final SQLsSyntax.KeyWordAscDesc aseWord = this.aseWord;
        if (aseWord != null) {
            sqlBuilder.append(aseWord.spaceWord);
        }

        if (this instanceof SortItemWithNullsOption) {
            sqlBuilder.append(((SortItemWithNullsOption) this).nullOption.spaceWords);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder()
                .append(this.sortItem);

        final SQLsSyntax.KeyWordAscDesc aseWord = this.aseWord;
        if (aseWord != null) {
            builder.append(aseWord.spaceWord);
        }

        if (this instanceof SortItemWithNullsOption) {
            builder.append(((SortItemWithNullsOption) this).nullOption.spaceWords);
        }
        return builder.toString();
    }


    private static final class SortItemWithNullsOption extends ArmySortItems {

        private final SQLs.KeyWordsNullsFirstLast nullOption;

        private SortItemWithNullsOption(ArmyExpression sortItem, @Nullable SQLs.KeyWordAscDesc aesWord
                , SQLs.KeyWordsNullsFirstLast nullOption) {
            super(sortItem, aesWord);
            this.nullOption = nullOption;
        }

    }//SortItemWithNullsOption

}
