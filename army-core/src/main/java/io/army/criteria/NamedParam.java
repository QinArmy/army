package io.army.criteria;

public interface NamedParam extends SqlParam, SqlValueParam.NamedValue {


    interface NamedMulti extends NamedParam, SqlValueParam.NamedMultiValue {

    }

    interface NamedSingle extends NamedParam, SqlValueParam.SingleValue {

    }

}
