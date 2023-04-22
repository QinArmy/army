package io.army.criteria.impl;

import io.army.criteria.Selection;
import io.army.criteria.impl.inner._DerivedTable;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;

import java.util.List;
import java.util.Map;

abstract class DialectFunctionUtils extends FunctionUtils {

    DialectFunctionUtils() {
    }


    static Functions._TabularFunction compositeTabularFunc(String name, List<?> argList, List<Selection> selectionList) {
        return new CompositeTabularFunction(name, argList, selectionList, null);
    }

    static Functions._TabularFunction compositeTabularFunc(String name, List<?> argList, List<Selection> selectionList,
                                                           Map<String, Selection> selectionMap) {
        return new CompositeTabularFunction(name, argList, selectionList, selectionMap);
    }

    /**
     * <p>
     * This class don't support {@link Functions._WithOrdinalityClause}.
     * </p>
     */
    private static final class CompositeTabularFunction implements Functions._TabularFunction, _DerivedTable,
            _SelfDescribed {

        private final String name;

        private final List<?> argList;

        private final List<Selection> selectionList;

        private Map<String, Selection> selectionMap;

        private CompositeTabularFunction(String name, List<?> argList, List<Selection> selectionList,
                                         @Nullable Map<String, Selection> selectionMap) {
            assert selectionMap == null || selectionMap.size() == selectionList.size();
            this.name = name;
            this.argList = argList;
            this.selectionList = selectionList;
            this.selectionMap = selectionMap;
        }

        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder sqlBuilder;
            sqlBuilder = context.sqlBuilder()
                    .append(_Constant.SPACE)
                    .append(this.name)
                    .append(_Constant.LEFT_PAREN);
            FunctionUtils.appendComplexArg(this.argList, context);
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
        }

        @Override
        public Selection refSelection(final @Nullable String name) {
            if (name == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            Map<String, Selection> selectionMap = this.selectionMap;
            if (selectionMap == null) {
                selectionMap = FunctionUtils.createSelectionMapFrom(null, this.selectionList);
                this.selectionMap = selectionMap;
            }
            return selectionMap.get(name);
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionList;
        }

    }//CompositeTabularFunction


}
