package io.army.option;

import java.util.Set;

public interface IterableOptionSpec extends OptionSpec {

    /**
     * @return a unmodified set
     */
    Set<Option<?>> optionSet();


}
