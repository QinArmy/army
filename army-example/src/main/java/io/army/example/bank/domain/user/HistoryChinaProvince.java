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

import io.army.annotation.*;

@Table(name = "history_china_province", indexes =
@Index(name = "history_china_province_uni_provincial_capital", fieldList = "provincialCapital", unique = true),
        comment = "history china province")
@DiscriminatorValue(RegionType.Constant.PROVINCE)
public class HistoryChinaProvince extends HistoryChinaRegion<HistoryChinaProvince> {


    @Column(precision = 80, notNull = true, comment = "china provincial capital")
    private String provincialCapital;

    @Column(precision = 80, notNull = true, defaultValue = "''", comment = "china provincial governor")
    private String governor;

    @Column(defaultValue = "0", notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "relation Id")
    private Long relationId;


    public String getProvincialCapital() {
        return provincialCapital;
    }

    public HistoryChinaProvince setProvincialCapital(String provincialCapital) {
        this.provincialCapital = provincialCapital;
        return this;
    }

    public String getGovernor() {
        return governor;
    }

    public HistoryChinaProvince setGovernor(String governor) {
        this.governor = governor;
        return this;
    }

    public Long getRelationId() {
        return relationId;
    }

    public HistoryChinaProvince setRelationId(Long relationId) {
        this.relationId = relationId;
        return this;
    }


}
