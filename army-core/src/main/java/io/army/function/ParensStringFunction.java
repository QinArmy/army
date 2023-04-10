package io.army.function;

import java.util.List;

@Deprecated
@FunctionalInterface
public interface ParensStringFunction {

    List<String> parens(String first, String... rest);

}
