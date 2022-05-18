package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util._CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * package class,for dialect syntax utils.
 * </p>
 *
 * @since 1.0
 */
abstract class Syntax extends Functions {

    /**
     * package constructor
     */
    Syntax() {
    }


    public static Cte cte(String name, SubStatement subStatement) {
        return new CteImpl(name, subStatement);
    }

    public static Cte cte(String name, List<String> aliasLst, SubStatement subStatement) {
        return new CteImpl(name, aliasLst, subStatement);
    }


    static final class CteImpl implements Cte {

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        private CteImpl(String name, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = Collections.emptyList();
            this.subStatement = subStatement;
        }


        private CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = _CollectionUtils.asUnmodifiableList(columnNameList);
            this.subStatement = subStatement;
        }

        @Override
        public String name() {
            return this.name;
        }

        @Override
        public List<String> columnNameList() {
            return this.columnNameList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<? extends SelectItem> selectItemList() {
            final SubStatement subStatement = this.subStatement;
            final List<? extends SelectItem> list;
            if (subStatement instanceof DerivedTable) {
                list = ((DerivedTable) subStatement).selectItemList();
            } else {
                list = Collections.emptyList();
            }
            return list;
        }

        @Override
        public Selection selection(final String derivedFieldName) {
            final SubStatement subStatement = this.subStatement;
            final Selection selection;
            if (subStatement instanceof DerivedTable) {
                selection = ((DerivedTable) subStatement).selection(derivedFieldName);
            } else {
                selection = null;
            }
            return selection;
        }


    }//CteImpl
}
