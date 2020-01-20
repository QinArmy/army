package io.army.dao;

import io.army.criteria.LockMode;

public interface ArmyBaseDao {

    /**
     * query all field of entity by primary key.
     *
     * @param entityClass entity class
     * @param id          primary key
     * @return the found entity instance or null if the entity does
     * not exist
     * @throws IllegalArgumentException if the first argument does
     *                                  not denote an entity type or the second argument is
     *                                  is not a valid type for that entity's primary key or
     *                                  is null
     */
    <T> T get(Class<T> entityClass, Object id);

    <T> T get(Class<T> entityClass, Object id, boolean withoutVisible);

    /**
     * query all field of entity by primary key and lock the row
     *
     * @param entityClass entity class
     * @param id          primary key
     * @param lockMode    lock mode
     * @return the found entity instance or null if the entity does
     * not exist
     * @throws IllegalArgumentException if the first argument does
     *                                  not denote an entity type or the second argument is
     *                                  is not a valid type for that entity's primary key or
     *                                  is null
     */
    <T> T get(Class<T> entityClass, Object id, LockMode lockMode);

    <T> T get(Class<T> entityClass, Object id, LockMode lockMode, boolean withoutVisible);


}