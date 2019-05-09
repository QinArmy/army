package com.example.criteria;

import com.example.domain.DebitOrder;
import com.example.domain.User;
import org.qinarmy.army.criteria.LockMode;
import org.qinarmy.army.criteria.impl.DLS;

/**
 * created  on 2018/10/21.
 */
public class CriteriaExpressionTests {


    public void simpleCriteria() {
        DLS.select()
                .from(User.class).as("u")
                .join(DebitOrder.class).as("d")
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
