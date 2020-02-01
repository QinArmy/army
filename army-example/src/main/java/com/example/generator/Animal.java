package com.example.generator;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "animal",comment = "动物表")
@DiscriminatorValue(BeingType.Constant.ANIMAL)
public class Animal extends Being {



}
