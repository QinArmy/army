package io.army.wrapper;


public interface ChildSQLWrapper extends SQLWrapper {

    SimpleSQLWrapper parentWrapper();

    SimpleSQLWrapper childWrapper();

    static ChildSQLWrapper build(SimpleSQLWrapper parentWrapper, SimpleSQLWrapper childWrapper) {
        return new ChildSQLWrapperImpl(parentWrapper, childWrapper);
    }

}
