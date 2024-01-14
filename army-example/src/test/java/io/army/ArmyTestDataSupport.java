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

package io.army;

import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import io.army.example.pill.domain.PillPerson;
import io.army.example.pill.domain.PillUser;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.PillUserType;
import io.army.util.ArrayUtils;
import io.army.util._Collections;
import io.army.util._StringUtils;
import org.testng.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ArmyTestDataSupport {

    protected ArmyTestDataSupport() {
    }

    private static final List<String> REGION_LIST = ArrayUtils.of(
            "绿叶港", "幽龙潭", "涡流岛", "元泱界", "曲境", "迷离谷", "万诗之海", "马鱼腮角", "雪谷海沟",
            "光之森林", "铁河流域", "长梦之河", "灵山塔", "舞阳河"//, "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> PROVINCE_LIST = ArrayUtils.of(
            "树国", "兽国", "龙国", "海国", "翼国", "雪国", "商国", "风国", "虫国", "沙国",
            "夜国", "谢都", "马林拉都", "勾雨港"//,"", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> CITY_LIST = ArrayUtils.of(
            "米拉都", "铁河郡", "游尾郡", "马驴耳城", "熊咆城", "獐腿城", "豹纹城", "孔屏城", "鲸鼻城", "首尾港",
            "铁湖城", "光荣城", "比邻都", "风之要塞", "常皙城", "言蹊城", "朝绯城", "始碧城", "呼啸1号城", "呼啸2号城",
            "呼啸3号城", "港口1号", "港口2号", "补给站1号"//,"", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> PERSON_LIST = ArrayUtils.of(
            "朴心", "魁拔", "幽弥狂", "奇衡三", "镜心", "蛮小满", "蛮吉", "谷鸡泰", "雪伦", "雷光",
            "燃谷", "幽若离", "大仓", "吧咕哒", "卡拉肖克·玲", "卡拉肖克·潘", "狄秋", "远浪", "离离艾", "白落提",
            "丰和", "桓泽金", "广秀", "英宋", "海问香", "脉兽秀秀", "玛朵布莎·爪云", "玛朵布莎·辞", "梅龙尼卡·嘉", "梅龙尼卡·蹄",
            "雷光", "紫霜", "鹿满", "狼勇", "熊枭", "秋落木", "离离茶", "花芫", "叶鹏", "泱",
            "勒克米罗·修", "梅龙尼卡·诚", "阿赫流瑟·兰", "迷麟", "卡拉肖克·飞", "卡拉肖克·雄", "玛朵布莎·小云", "万两", "玛朵布莎·白", "天宠",
            "梅零落", "尘", "古斯达"
            // "","","","","", "" , "","","",""
            // "","","","","", "" , "","","",""
    );

    private static final List<String> NATION_LIST = ArrayUtils.of(
            "龙族", "兽族", "辉妖族", "独行族", "特得克族", "神族", "粼妖", "格洛莫赫人", "基思卡人", "翼族",
            "萨库人", "蛰族", "默拓人" //,"","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",

    );

    protected final String randomProvince() {
        return randomProvince(ThreadLocalRandom.current());
    }

    protected final String randomProvince(Random random) {
        return randomSuffix(random, PROVINCE_LIST.get(random.nextInt(Integer.MAX_VALUE) % PROVINCE_LIST.size()))
                .toString();
    }

    protected final String randomRegion() {
        return randomRegion(ThreadLocalRandom.current());
    }

    protected final String randomRegion(Random random) {
        return randomSuffix(random, REGION_LIST.get(random.nextInt(Integer.MAX_VALUE) % REGION_LIST.size()))
                .toString();
    }

    protected final String randomCity() {
        return randomCity(ThreadLocalRandom.current());
    }

    protected final String randomCity(Random random) {
        return randomSuffix(random, CITY_LIST.get(random.nextInt(Integer.MAX_VALUE) % CITY_LIST.size()))
                .toString();

    }


    protected final String randomPerson() {
        return randomPerson(ThreadLocalRandom.current());
    }

    protected final String randomPerson(Random random) {
        return randomSuffix(random, PERSON_LIST.get(random.nextInt(Integer.MAX_VALUE) % PERSON_LIST.size()))
                .toString();
    }

    protected final String randomNation() {
        return randomNation(ThreadLocalRandom.current());
    }

    protected final String randomNation(Random random) {
        return randomSuffix(random, NATION_LIST.get(random.nextInt(Integer.MAX_VALUE) % NATION_LIST.size()))
                .toString();
    }

    protected final BigDecimal randomDecimal() {
        return randomDecimal(ThreadLocalRandom.current());
    }

    protected final BigDecimal randomDecimal(Random random) {
        return BigDecimal.valueOf(random.nextDouble() * 999999)
                .setScale(2, RoundingMode.HALF_UP);
    }


    protected final List<ChinaRegion<?>> createReginList() {
        final Random random = ThreadLocalRandom.current();
        final List<ChinaRegion<?>> list = _Collections.arrayList();
        ChinaRegion<?> c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaRegion<>()
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setName(randomRegion(random))
                    .setRegionType(RegionType.NONE)
                    .setRegionGdp(randomDecimal(random))

                    .setVersion(0)
                    .setVisible(Boolean.TRUE);

            list.add(c);
        }
        return list;
    }


    protected final List<ChinaCity> createCityList() {
        final Random random = ThreadLocalRandom.current();
        final List<ChinaCity> list = _Collections.arrayList();
        ChinaCity c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaCity()
                    .setId(Math.abs(random.nextLong()))
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setName(randomCity(random))
                    .setRegionType(RegionType.CITY)
                    .setRegionGdp(randomDecimal(random))

                    .setVersion(0)
                    .setVisible(Boolean.TRUE)

                    .setMayorName(randomPerson(random));

            list.add(c);
        }
        return list;
    }

    protected final List<ChinaProvince> createProvinceList() {
        final Random random = ThreadLocalRandom.current();
        final List<ChinaProvince> list = _Collections.arrayList();
        ChinaProvince c;
        final int rowSize = 5;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaProvince()
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setName(randomProvince(random))
                    .setRegionType(RegionType.CITY)
                    .setRegionGdp(randomDecimal(random))

                    .setVersion(0)
                    .setVisible(Boolean.TRUE)

                    .setGovernor(randomPerson(random))
                    .setProvincialCapital(randomCity(random));

            list.add(c);
        }
        return list;
    }

    protected final ChinaProvince createRandomProvince() {
        final Random random = ThreadLocalRandom.current();
        final LocalDateTime now = LocalDateTime.now();
        return new ChinaProvince()
                .setId(Math.abs(random.nextLong()))
                .setCreateTime(now)
                .setUpdateTime(now)

                .setName(randomProvince(random))
                .setRegionType(RegionType.CITY)
                .setRegionGdp(randomDecimal(random))

                .setVersion(0)
                .setVisible(Boolean.TRUE)

                .setGovernor(randomPerson(random))
                .setProvincialCapital(randomPerson(random));
    }


    protected final List<PillPerson> createPersonList() {
        final List<PillPerson> list = _Collections.arrayList();
        PillPerson u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillPerson();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("脉兽" + 1);
            u.setBirthday(LocalDate.now());

            list.add(u);

        }
        return list;
    }


    protected final List<PillUser<?>> createUserList() {
        final List<PillUser<?>> list = _Collections.arrayList();
        PillUser<?> u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillUser<>();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("脉兽" + 1);

            list.add(u);

        }
        return list;
    }


    protected final List<BankUser<?>> createBankUserList() {
        final List<BankUser<?>> list = _Collections.arrayList();
        BankUser<?> u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            u = new BankUser<>()
                    .setId((long) i)
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setNickName("妖侠" + i)
                    .setUserType(BankUserType.BANK)
                    .setPartnerUserId(0L)
                    .setCompleteTime(now.minusDays(1))

                    .setCertificateId(0L)
                    .setUserNo(Integer.toString(i + 9999))
                    .setRegisterRecordId(0L)
                    .setVersion(0)

                    .setVisible(Boolean.TRUE);

            list.add(u);
        }
        return list;
    }


    protected static void assertChinaRegionAfterNoConflictInsert(final List<? extends ChinaRegion<?>> regionList) {
        Long id, lastId = null;
        for (ChinaRegion<?> chinaRegion : regionList) {
            id = chinaRegion.getId();
            Assert.assertNotNull(id);
            if (lastId != null) {
                Assert.assertTrue(id > lastId);
            }
            lastId = id;
        }

    }

    /**
     * @return a unmodified list
     */
    protected static List<Dialect> createDialectList(final List<Database> databaseList) {
        final List<Dialect> dialectList = _Collections.arrayList();
        for (Database database : databaseList) {
            dialectList.addAll(Arrays.asList(database.dialects()));
        }
        return Collections.unmodifiableList(dialectList);
    }

    private static StringBuilder randomSuffix(final Random random, final String value) {
        return _StringUtils.builder()
                .append(value)
                .append('@')
                .append(Instant.now())
                .append('#')
                .append((random.nextLong() % 100))
                .append('#')
                .append(random.nextInt(Integer.MAX_VALUE) % 100);
    }


}
