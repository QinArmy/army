/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.session;

import javax.annotation.Nullable;

/**
 * <p>Throw(or emit) by driver spi (for example {@code java.sql.Connection} , {@code io.jdbd.session.DatabaseSession} )<br/>
 * when database driver throw {@link Throwable}
 * <p> {@link ServerException} is important sub-class.
 *
 * @see ServerException
 * @since 0.6.0
 */
public class DriverException extends DataAccessException {

    private final String sqlState;

    private final int vendorCode;

    public DriverException(String message, Throwable cause, @Nullable String sqlState, int vendorCode) {
        super(message, cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }

    public DriverException(Throwable cause, @Nullable String sqlState, int vendorCode) {
        super(cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }


    @Nullable
    public final String getSqlState() {
        return this.sqlState;
    }


    public final int getVendorCode() {
        return this.vendorCode;
    }


}
