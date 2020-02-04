package io.army.criteria;

import io.army.meta.mapping.MappingType;

import java.sql.JDBCType;

/**
 * 代表 select 列表中的元素
 * created  on 2018/10/8.
 */
public interface Selection {

    default MappingType mappingType(){
        throw new UnsupportedOperationException();
    }

   default Class<?> javaType(){
       throw new UnsupportedOperationException();
    }

   default JDBCType jdbcType(){
       throw new UnsupportedOperationException();
   }

    @Override
    String toString();


}
