package io.army.dialect;

/**
 * <p>
 * This interface representing database dialect version.
 *
 * @since 0.6.0
 */
public interface Dialect {

    /**
     * @return dialect version name
     */
    String name();

    /**
     * @return the database of this dialect version.
     */
    Database database();

    /**
     * @return <ul>
     * <li>positive : this version greater than o</li>
     * <li>zero : this and o is same instance.</li>
     * <li>negative : this version less than o</li>
     * </ul>
     * @throws IllegalArgumentException throw when o isn't family with this.
     */
    int compareWith(Dialect o) throws IllegalArgumentException;

    /**
     * @return true : this and o is same dialect family.
     */
    boolean isFamily(Dialect o);

    @Override
    String toString();


}
