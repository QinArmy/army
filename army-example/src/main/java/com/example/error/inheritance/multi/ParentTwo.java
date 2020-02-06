package com.example.error.inheritance.multi;

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "parent_two", comment = "two parentMeta")
@DiscriminatorValue(MultiParentType.Constant.TWO)
public class ParentTwo extends ParentOne {

    @Column(comment = "two")
    private String two;

    public String getTwo() {
        return two;
    }

    public ParentTwo setTwo(String two) {
        this.two = two;
        return this;
    }
}
