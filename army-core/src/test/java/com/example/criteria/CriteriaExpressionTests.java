package com.example.criteria;

import com.example.domain.DebitOrder;
import com.example.domain.User;
import io.army.criteria.LockMode;
import io.army.criteria.impl.DLS;

/**
 * created  on 2018/10/21.
 */
public class CriteriaExpressionTests {


    public void simpleCriteria() {
        DLS.select()
                .from(User.class).as("u")
                .join(DebitOrder.class).as("d").on()
                .where()
                .group()
                .having()
                .order()
                .limit(9)
                .setLockMode(LockMode.NONE)
                .build()


        ;

    }


}
