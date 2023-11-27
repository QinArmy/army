package io.army.session;

import javax.annotation.Nullable;
import java.util.function.Function;


/**
 * <p>This class representing server error message.
 * <p>Throw(or emit) when database server response error message.
 *
 * @since 1.0
 */
public final class ServerException extends DriverException implements OptionSpec {

    private final Function<ArmyOption<?>, ?> optionFunc;


    public ServerException(Throwable cause, @Nullable String sqlState, int vendorCode,
                           Function<ArmyOption<?>, ?> optionFunc) {
        super(cause, sqlState, vendorCode);
        this.optionFunc = optionFunc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(ArmyOption<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


}
