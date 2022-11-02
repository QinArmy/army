package io.army.dialect;

public interface _ValueInsertContext extends _InsertContext
        , _InsertContext._ValueSyntaxSpec {


    void appendReturnIdIfNeed();

}
