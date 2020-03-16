package io.army.criteria.impl;

import io.army.criteria.AliasTableFieldMeta;

interface CriteriaContext {

     AliasTableFieldMeta<?,?> aliasField(String tableAlias,String propName);


}
