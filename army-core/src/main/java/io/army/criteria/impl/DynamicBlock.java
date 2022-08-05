package io.army.criteria.impl;

import io.army.criteria.CrossItemBlock;
import io.army.criteria.JoinItemBlock;
import io.army.criteria.TableItem;

abstract class DynamicBlock implements JoinItemBlock, CrossItemBlock, TableBlock.BlockParams {


    @Override
    public final _JoinType joinType() {
        return null;
    }

    @Override
    public final TableItem tableItem() {
        return null;
    }

    @Override
    public final String alias() {
        return null;
    }


}
