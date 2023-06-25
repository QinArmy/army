package io.army.criteria.impl.inner;


public interface _SingleUpdate extends _Update, _SingleDml {


    interface _ChildUpdate extends _SingleUpdate, _ChildStatement {

        @Override
        _SingleUpdate parentStmt();

    }


}
