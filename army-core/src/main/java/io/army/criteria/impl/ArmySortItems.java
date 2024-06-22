/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.annotation.SortOrder;
import io.army.criteria.SortItem;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;

class ArmySortItems implements ArmySortItem {

    static ArmySortItem create(final ArmyExpression exp, final SortOrder order,
                               final @Nullable SQLs.NullsFirstLast firstLast) {
        final ArmySortItem sortItem;
        if (firstLast == null) {
            sortItem = new ArmySortItems(exp, order);
        } else if (firstLast != SQLs.NULLS_LAST && firstLast != SQLs.NULLS_FIRST) {
            throw CriteriaUtils.unknownWords(firstLast);
        } else {
            sortItem = new SortItemWithNullsOption(exp, order, firstLast);
        }
        return sortItem;
    }


    final ArmyExpression sortItem;

    private final SortOrder order;

    private ArmySortItems(ArmyExpression sortItem, SortOrder order) {
        this.sortItem = sortItem;
        this.order = order;
    }

    @Override
    public final TypeMeta typeMeta() {
        return this.sortItem.typeMeta();
    }

    @Override
    public final SortItem asSortItem() {
        //always this
        return this;
    }

    @Override
    public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
        this.sortItem.appendSql(sqlBuilder, context);

        if (this.order != SortOrder.DEFAULT) {
            sqlBuilder.append(_Constant.SPACE)
                    .append(this.order.name());
        }
        if (this instanceof SortItemWithNullsOption) {
            sqlBuilder.append(((SortItemWithNullsOption) this).nullOption.spaceRender());
        }
    }

    @Override
    public final String toString() {
        final StringBuilder builder;
        builder = new StringBuilder()
                .append(this.sortItem);

        if (this.order != SortOrder.DEFAULT) {
            builder.append(_Constant.SPACE)
                    .append(this.order.name());
        }

        if (this instanceof SortItemWithNullsOption) {
            builder.append(((SortItemWithNullsOption) this).nullOption.spaceRender());
        }
        return builder.toString();
    }


    private static final class SortItemWithNullsOption extends ArmySortItems {

        private final SQLs.NullsFirstLast nullOption;

        private SortItemWithNullsOption(ArmyExpression sortItem, SortOrder order,
                                        SQLs.NullsFirstLast nullOption) {
            super(sortItem, order);
            this.nullOption = nullOption;
        }

    }//SortItemWithNullsOption


}
