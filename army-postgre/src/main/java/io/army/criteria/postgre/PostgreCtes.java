package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

/**
 * <p>
 * This interface representing Postgre cte builder.
 * </p>
 *
 * @since 1.0
 */
public interface PostgreCtes extends CteBuilderSpec {

   /**
    * <p>
    * create new cte that create new single-table INSERT statement that is sub insert statement in dynamic with clause.
    * </p>
    *
    * @param name cte name
    */
   PostgreInsert._DynamicCteParensSpec subSingleInsert(String name);

   /**
    * <p>
    * create new cte that create new single-table UPDATE statement that is sub insert statement in dynamic with clause.
    * </p>
    *
    * @param name cte name
    */
   PostgreUpdate._DynamicCteParensSpec subSingleUpdate(String name);

   /**
    * <p>
    * create new cte that create new single-table DELETE statement that is sub insert statement in dynamic with clause.
    * </p>
    *
    * @param name cte name
    */
   PostgreDelete._DynamicCteParensSpec subSingleDelete(String name);

   PostgreQuery._DynamicCteParensSpec subQuery(String name);

   PostgreValues._DynamicCteParensSpec subValues(String name);


}
