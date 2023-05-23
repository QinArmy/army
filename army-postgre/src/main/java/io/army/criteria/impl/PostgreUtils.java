package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.UndoneFunction;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._FunctionField;
import io.army.criteria.impl.inner._ParensRowSet;
import io.army.criteria.impl.inner._RowSet;
import io.army.criteria.postgre.PostgreStatement;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.sqltype.PostgreDataType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

abstract class PostgreUtils extends CriteriaUtils {

    private PostgreUtils() {
    }


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

    /*-------------------below inner class -------------------*/

    private static final class FuncColumnDefinitionClause
            implements PostgreStatement._FuncColumnDefinitionSpaceClause,
            PostgreStatement.FuncColumnDefCommaClause {

        private List<_FunctionField> fieldList = _Collections.arrayList();

        private Map<String, _FunctionField> fieldMap = _Collections.hashMap();

        private Boolean state;

        private FuncColumnDefinitionClause() {
        }

        @Override
        public PostgreStatement.FuncColumnDefCommaClause space(String name, MappingType type) {
            if (this.state != null) {
                throw CriteriaUtils.spaceMethodNotFirst();
            }
            this.state = Boolean.TRUE;
            return this.comma(name, type);
        }

        @Override
        public PostgreStatement.FuncColumnDefCommaClause comma(final @Nullable String name,
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
            field = DialectFunctionUtils.funcField(name, type, PostgreDataType.class);
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


    static final class DoneFunc {

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


    }//PostgreDoneFunc


}
