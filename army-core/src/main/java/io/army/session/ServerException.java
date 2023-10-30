package io.army.session;

import javax.annotation.Nullable;

import java.util.function.Function;


/**
 * <p>This class representing server error message.
 * <p>Throw when database server response error message.
 *
 * @since 1.0
 */
public final class ServerException extends DataAccessException implements OptionSpec {

    private final String sqlState;

    private final int vendorCode;

    private final Function<Option<?>, ?> optionFunc;

    public ServerException(Throwable cause, @Nullable String sqlState, int vendorCode,
                           Function<Option<?>, ?> optionFunc) {
        super(cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
        this.optionFunc = optionFunc;
    }


    @Nullable
    public String getSqlState() {
        return this.sqlState;
    }


    public int getVendorCode() {
        return this.vendorCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T valueOf(Option<T> option) {
        final Object value;
        value = this.optionFunc.apply(option);
        if (option.javaType().isInstance(value)) {
            return (T) value;
        }
        return null;
    }


}
