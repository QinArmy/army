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
import io.army.example.common.BaseVersionDomain;

import java.math.BigDecimal;

@MappedSuperclass
@SuppressWarnings("unchecked")
public class AbstractChinaRegion<T extends AbstractChinaRegion<T>> extends BaseVersionDomain<T> {


    @Generator(type = GeneratorType.POST)
    @Column
    private Long id;

    @Column
    private RegionType regionType;

    @Column(precision = 60, nullable = false, comment = "china region name")
    private String name;

    @Column(precision = 16, scale = 2, defaultValue = "0.00", nullable = false, comment = "china region GDP")
    private BigDecimal regionGdp;


    @Column(nullable = false, defaultValue = "0", updateMode = UpdateMode.IMMUTABLE, comment = "china region parent level id")
    private Long parentId;

    @Column(defaultValue = "0", nullable = false, comment = "china region population")
    private Integer population;

    @Override
    public Long getId() {
        return this.id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public T setRegionType(RegionType regionType) {
        this.regionType = regionType;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public BigDecimal getRegionGdp() {
        return regionGdp;
    }

    public T setRegionGdp(BigDecimal regionGdp) {
        this.regionGdp = regionGdp;
        return (T) this;
    }

    public Long getParentId() {
        return parentId;
    }

    public T setParentId(Long parentId) {
        this.parentId = parentId;
        return (T) this;
    }

    public Integer getPopulation() {
        return population;
    }

    public T setPopulation(Integer population) {
        this.population = population;
        return (T) this;
    }


}
