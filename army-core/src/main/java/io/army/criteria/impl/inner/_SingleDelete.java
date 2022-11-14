package io.army.criteria.impl.inner;

public interface _SingleDelete extends _Delete, _SingleDml {


    interface _ChildDelete extends _SingleDelete, _ChildStatement {

        _SingleDelete parentStmt();

    }

}
