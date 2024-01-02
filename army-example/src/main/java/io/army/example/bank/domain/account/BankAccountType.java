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

package io.army.example.bank.domain.account;

import io.army.struct.CodeEnum;

public enum BankAccountType implements CodeEnum {

    BANK(Constant.BANK),
    LENDER(Constant.LENDER),
    BORROWER(Constant.BORROWER),
    PARTNER(Constant.PARTNER),
    LENDER_BUSINESS(Constant.LENDER_BUSINESS),
    BORROWER_BUSINESS(Constant.BORROWER_BUSINESS),
    GUARANTOR(Constant.GUARANTOR);


    private final short code;

    BankAccountType(short code) {
        this.code = code;
    }

    @Override
    public final int code() {
        return this.code;
    }

    interface Constant {

        short BANK = 0;
        short LENDER = 100;
        short BORROWER = 200;
        short PARTNER = 300;
        short LENDER_BUSINESS = 400;
        short BORROWER_BUSINESS = 500;
        short GUARANTOR = 600;

    }


}
