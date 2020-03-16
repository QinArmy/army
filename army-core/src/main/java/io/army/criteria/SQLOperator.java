package io.army.criteria;

import io.army.lang.Nullable;

/**
 * created  on 2018/11/25.
 */
public interface SQLOperator {


    String rendered();

   default boolean relational(){
       return false;
   }

   default Position position(){
       return Position.CENTER;
   }


    enum Position {
        LEFT,
        CENTER,
        RIGHT,
        TWO,
    }

}
