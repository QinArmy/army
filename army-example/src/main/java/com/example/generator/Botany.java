package com.example.generator;

import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "botany",comment = "植物表")
@DiscriminatorValue(BeingType.Constant.BOTANY)
public class Botany extends Being {

}
