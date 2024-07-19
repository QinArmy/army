package io.army.spec;

import io.army.option.Option;

import java.util.Set;

public interface IterableOptionSpec extends OptionSpec {

    /**
     * @return a unmodified set
     */
    Set<Option<?>> optionSet();


}
