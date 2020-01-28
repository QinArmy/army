package com.example.error.inheritance.multi;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;

//@Table(name = "parent_three", comment = "three parent")
//@Inheritance("mode")
public class ParentThree extends ParentTwo {

    @Column(defaultValue = "" + MultiParentType.Constant.THREE,comment = "mode")
    private MultiParentType mode;

    public MultiParentType getMode() {
        return mode;
    }

    public ParentThree setMode(MultiParentType mode) {
        this.mode = mode;
        return this;
    }
}
