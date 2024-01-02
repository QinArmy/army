/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.postgre;

import io.army.criteria.CteBuilderSpec;

/**
 * <p>
 * This interface representing Postgre cte builder.
 *
 * @since 0.6.0
 */
public interface PostgreCtes extends CteBuilderSpec {

   /**
    * <p>
    * create new cte that create new single-table INSERT statement that is sub insert statement in dynamic with clause.
    *
    *
    * @param name cte name
    */
   PostgreInsert._DynamicCteParensSpec subSingleInsert(String name);

   /**
    * <p>
    * create new cte that create new single-table UPDATE statement that is sub insert statement in dynamic with clause.
    *
    *
    * @param name cte name
    */
   PostgreUpdate._DynamicCteParensSpec subSingleUpdate(String name);

   /**
    * <p>
    * create new cte that create new single-table DELETE statement that is sub insert statement in dynamic with clause.
    *
    *
    * @param name cte name
    */
   PostgreDelete._DynamicCteParensSpec subSingleDelete(String name);

   PostgreQuery._DynamicCteParensSpec subQuery(String name);

   PostgreValues._DynamicCteParensSpec subValues(String name);


}
