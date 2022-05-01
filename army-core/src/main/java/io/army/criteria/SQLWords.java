package io.army.criteria;

public interface SQLWords {

    /**
     * @return one space char and sql keywords.
     */
    String render();

    @Override
    String toString();


    interface Modifier extends SQLWords {

        int level();

    }


}
