package io.army.criteria;

public interface NamedParam extends SQLParam, SqlValueParam.NamedValue {


    interface NamedMulti extends NamedParam, SqlValueParam.NamedMultiValue {

    }

    interface NamedSingle extends NamedParam, SqlValueParam.SingleValue {

    }

}
