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

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;

@Table(name = "u_bank_partner", comment = "bank partner enterprise")
@DiscriminatorValue(BankUserType.Constant.PARTNER)
public class PartnerUser extends BankUser<PartnerUser> {

    @Column(comment = "invest partner legal person user id")
    private Long legalPersonId;

    @Column(comment = "invest partner china region id")
    private Long cityId;

    public final Long getLegalPersonId() {
        return legalPersonId;
    }

    public final PartnerUser setLegalPersonId(Long legalPersonId) {
        this.legalPersonId = legalPersonId;
        return this;
    }

    public final Long getCityId() {
        return cityId;
    }

    public final PartnerUser setCityId(Long cityId) {
        this.cityId = cityId;
        return this;
    }


}
