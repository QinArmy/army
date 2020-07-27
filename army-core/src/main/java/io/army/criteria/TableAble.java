package io.army.criteria;

public interface TableAble  {


    /**
     * @return text of tale.
     */
    @Override
    String toString();

    /**
     * Consistent with {@link Object#hashCode()}
     */
    @Override
    int hashCode();

    /**
     * Consistent with {@link Object#equals(Object)}
     */
    @Override
    boolean equals(Object o);
}
