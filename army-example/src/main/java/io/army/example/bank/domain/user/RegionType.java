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

package io.army.example.bank.domain.user;

import io.army.struct.CodeEnum;

public enum RegionType implements CodeEnum {

    NONE(Constant.NONE),
    PROVINCE(Constant.PROVINCE),
    CITY(Constant.CITY);

    private final byte codeValue;

    RegionType(final int codeValue) {
        assert codeValue >= Byte.MIN_VALUE && codeValue <= Byte.MAX_VALUE;
        this.codeValue = (byte) codeValue;
    }

    @Override
    public final int code() {
        return this.codeValue;
    }


    interface Constant {

        byte NONE = 0;
        byte PROVINCE = 10;
        byte CITY = 20;

    }


}
