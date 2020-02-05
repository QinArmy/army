package io.army.criteria.impl;

import io.army.lang.Nullable;

/**
 * created  on 2018/11/25.
 */
interface SQLOperator {


    String rendered();

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
