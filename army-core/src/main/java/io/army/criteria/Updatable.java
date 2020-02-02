package io.army.criteria;


public interface Updatable  extends SQLBuilder {

    int updateRow();

    long updateLarge();

}
