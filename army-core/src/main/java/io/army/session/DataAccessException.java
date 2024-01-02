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

/**
 * <p>Throw(or emit) by {@link Session} or {@link Cursor} when access database occur.
 * <p>Following
 * <ul>
 *     <li>{@link DriverException}</li>
 *     <li>{@link OptimisticLockException}</li>
 *     <li>{@link ChildUpdateException}</li>
 * </ul>
 * are  important sub-class
 *
 * @see DriverException
 * @since 0.6.0
 */
public class DataAccessException extends SessionException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }


}
