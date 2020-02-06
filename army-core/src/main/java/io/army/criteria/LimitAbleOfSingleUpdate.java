package io.army.criteria;

public interface LimitAbleOfSingleUpdate extends SingleUpdateAble,SQLBuilder {

    SingleUpdateAble limit(int rowCount);

}
