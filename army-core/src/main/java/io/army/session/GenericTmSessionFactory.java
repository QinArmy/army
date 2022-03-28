package io.army.session;

import io.army.Database;

import java.util.List;

/**
 * This interface is base interface of below:
 * <ul>
 *     <li>{@code io.army.sync.TmSessionFactory}</li>
 *     <li>{@code io.army.reactive.ReactiveTmSessionFactory}</li>
 * </ul>
 */
public interface GenericTmSessionFactory extends GenericSessionFactory {

    boolean supportZone();

    /**
     * @return a unmodifiable list
     */
    @Deprecated
    List<Database> rmServerMetaList();

    /**
     * @return a integer than great than 0 .
     */
    int tableCountPerDatabase();

}
