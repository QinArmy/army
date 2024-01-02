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
import io.army.annotation.Index;
import io.army.annotation.Table;

@Table(name = "china_province", indexes =
@Index(name = "uni_provincial_capital", fieldList = "provincialCapital", unique = true)
        , comment = "china province")
@DiscriminatorValue(RegionType.Constant.PROVINCE)
public class ChinaProvince extends ChinaRegion<ChinaProvince> {

    @Column(precision = 80, nullable = false, comment = "china provincial capital")
    private String provincialCapital;

    @Column(precision = 80, nullable = false, defaultValue = "''", comment = "china provincial governor")
    private String governor;


    public String getProvincialCapital() {
        return provincialCapital;
    }

    public ChinaProvince setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
        return this;
    }

    public String getGovernor() {
        return governor;
    }

    public ChinaProvince setGovernor(String governor) {
        this.governor = governor;
        return this;
    }
}
