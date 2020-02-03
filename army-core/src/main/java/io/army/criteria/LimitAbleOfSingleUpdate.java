package io.army.criteria;

public interface LimitAbleOfSingleUpdate extends Updatable {

    Updatable limit(int rowCount);

}
