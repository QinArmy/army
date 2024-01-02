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

package io.army.example.bank.bean;

import io.army.struct.CodeEnum;

public enum BankCode implements CodeEnum {

    OK(0, "SUCCESS"),

    REQUEST_NO_INVALID(440, "requestNo is invalid"),
    CAPTCHA_NOT_MATCH(441, "captcha not match"),
    REGION_NOT_EXISTS(442, "region not exists"),

    UNKNOWN_ERROR(540, "unknown error"),
    RECORD_FAILED(541, "record have failed"),

    PARTNER_NOT_EXISTS(1001, "partner not exits"),

    ACCOUNT_DUPLICATION(2001, "account not exits"),
    USER_DUPLICATION(2002, "partner user duplication"),

    REQUEST_OUT_DEADLINE(3001, "request out deadline");

    public final int code;

    public final String message;

    BankCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


    @Override
    public final int code() {
        return this.code;
    }


}
