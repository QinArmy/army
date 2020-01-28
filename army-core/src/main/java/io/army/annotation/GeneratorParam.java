package io.army.annotation;

import io.army.generator.PreMultiGenerator;

/**
 * @see Generator
 * @see PreMultiGenerator
 */
public @interface GeneratorParam {

    String name() ;

    String value();
}
