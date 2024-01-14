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

import io.army.criteria.Item;
import io.army.criteria.SubQuery;
import io.army.criteria.UndoneFunction;
import io.army.criteria.impl.inner._FunctionField;
import io.army.criteria.impl.inner._ParensRowSet;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.postgre.FuncColumnDefCommaClause;
import io.army.criteria.postgre.PostgreStatement;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.dialect.postgre.PostgreDialect;
import io.army.mapping.MappingType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }

    /**
     * reference last dialect
     */
    static final PostgreDialect DIALECT = PostgreDialect.POSTGRE16;


    static boolean isUnionQuery(final SubQuery query) {
        _RowSet rowSet = (_RowSet) query;
        while (rowSet instanceof _ParensRowSet) {
            rowSet = ((_ParensRowSet) rowSet).innerRowSet();
        }
        return rowSet instanceof SimpleQueries.UnionSubQuery;
    }


    static <R extends Item> PostgreStatement._FuncColumnDefinitionParensClause<R> undoneFunc(
            final UndoneFunction func, final Function<DoneFunc, R> function) {
        return c -> {
            final FuncColumnDefinitionClause clause;
            clause = new FuncColumnDefinitionClause();

            c.accept(clause);

            clause.endClause(); // end clause
            return function.apply(new DoneFunc(func, clause.fieldList, clause.fieldMap));
        };
    }

    static <T, R> Function<Consumer<PostgreStatement._FuncColumnDefinitionSpaceClause>, R> rowsFromUndoneFunc(
            final UndoneFunction func, final Function<DoneFunc, R> function) {
        return c -> {
            final FuncColumnDefinitionClause clause;
            clause = new FuncColumnDefinitionClause();

            c.accept(clause);

            clause.endClause(); // end clause
            return function.apply(new DoneFunc(func, clause.fieldList, clause.fieldMap));
        };
    }

    /*-------------------below inner class -------------------*/

    private static final class FuncColumnDefinitionClause
            implements PostgreStatement._FuncColumnDefinitionSpaceClause,
            FuncColumnDefCommaClause {

        private List<_FunctionField> fieldList = _Collections.arrayList();

        private Map<String, _FunctionField> fieldMap = _Collections.hashMap();

        private Boolean state;

        private FuncColumnDefinitionClause() {
        }

        @Override
        public FuncColumnDefCommaClause space(String name, MappingType type) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.comma(name, type);
        }

        @Override
        public FuncColumnDefCommaClause comma(final @Nullable String name,
                                              @Nullable final MappingType type) {
            final List<_FunctionField> fieldList = this.fieldList;
            if (this.state != Boolean.TRUE || !(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (name == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (type == null) {
                throw ContextStack.clearStackAndNullPointer();
            } else if (!_StringUtils.hasText(name)) {
                throw CriteriaUtils.funcFieldNameNoText();
            }
            final Map<String, _FunctionField> fieldMap = this.fieldMap;

            final _FunctionField field;
            field = DialectFunctionUtils.funcField(name, type);
            if (fieldMap.putIfAbsent(name, field) != null) {
                throw CriteriaUtils.funcFieldDuplication(name);
            }
            fieldList.add(field);
            return this;
        }


        void endClause() {
            final List<_FunctionField> fieldList = this.fieldList;
            if (!(fieldList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            } else if (fieldList.size() == 0) {
                throw ContextStack.clearStackAndCriteriaError("you don't add any column definition.");
            }
            this.fieldList = _Collections.unmodifiableList(fieldList);
            this.fieldMap = _Collections.unmodifiableMap(this.fieldMap);
            this.state = Boolean.FALSE;
        }

    }//FuncColumnDefinitionClause


    static final class DoneFunc implements ArmySQLFunction {

        final UndoneFunction funcItem;

        final List<_FunctionField> fieldList;

        final Map<String, _FunctionField> fieldMap;

        /**
         * @param fieldList unmodified list
         * @param fieldMap  unmodified map
         */
        private DoneFunc(UndoneFunction funcItem, List<_FunctionField> fieldList,
                         Map<String, _FunctionField> fieldMap) {
            this.funcItem = funcItem;
            this.fieldList = fieldList;
            this.fieldMap = fieldMap;

        }

        @Override
        public String name() {
            return this.funcItem.name();
        }

        /**
         * this method for ROWS FROM( ... ) syntax.
         */
        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            ((ArmySQLFunction) this.funcItem).appendSql(sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_AS)
                    .append(_Constant.LEFT_PAREN);
            CriteriaUtils.appendSelfDescribedList(this.fieldList, sqlBuilder, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.funcItem)
                    .append(_Constant.SPACE_AS)
                    .append(_Constant.LEFT_PAREN);

            CriteriaUtils.selfDescribedListToString(this.fieldList, builder);

            return builder.append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//PostgreDoneFunc


}
