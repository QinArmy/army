package io.army.annotation;

import io.army.generator.MultiGenerator;

/**
 * @see Generator
 * @see MultiGenerator
 */
public @interface GeneratorParam {

    String name() ;

    String value();
}
