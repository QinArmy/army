package io.army.criteria.impl;

import io.army.criteria.TablePart;

abstract class MySQLSingleBlock extends TableBlock {

    final String alias;

    MySQLSingleBlock(TablePart tablePart, JoinType joinType, String alias) {
        super(tablePart, joinType);
        this.alias = alias;
    }


}
