package io.army.criteria;

import java.sql.JDBCType;

/**
 * 代表 select 列表中的元素
 * created  on 2018/10/8.
 */
public interface Selection {

    Class<?> javaType();

   default JDBCType jdbcType(){
       throw new UnsupportedOperationException();
   }

    @Override
    String toString();


}
