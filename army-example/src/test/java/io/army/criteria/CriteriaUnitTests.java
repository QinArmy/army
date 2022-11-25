package io.army.criteria;

import io.army.example.bank.domain.user.ChinaCity;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.RegionType;
import io.army.util.ArrayUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class CriteriaUnitTests {

    private static final List<String> REGION_LIST = ArrayUtils.asUnmodifiableList(
            "绿叶港", "幽龙潭", "涡流岛", "元泱界", "曲境", "迷离谷", "万诗之海", "马鱼腮角", "雪谷海沟",
            "光之森林", "铁河流域", "长梦之河", "灵山塔", "舞阳河"//, "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> PROVINCE_LIST = ArrayUtils.asUnmodifiableList(
            "树国", "兽国", "龙国", "海国", "翼国", "雪国", "商国", "风国", "虫国", "沙国",
            "夜国", "谢都", "马林拉都", "勾雨港"//,"", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> CITY_LIST = ArrayUtils.asUnmodifiableList(
            "米拉都", "铁河郡", "游尾郡", "马驴耳城", "熊咆城", "獐腿城", "豹纹城", "孔屏城", "鲸鼻城", "首尾港",
            "铁湖城", "光荣城", "比邻都", "风之要塞", "常皙城", "言蹊城", "朝绯城", "始碧城", "呼啸1号城", "呼啸2号城",
            "呼啸3号城", "港口1号", "港口2号", "补给站1号"//,"", "" , "","","","",
            // "","","","","", "" , "","","","",
            // "","","","","", "" , "","","","",
    );

    private static final List<String> PERSON_LIST = ArrayUtils.asUnmodifiableList(
            "朴心", "魁拔", "幽弥狂", "奇衡三", "镜心", "蛮小满", "蛮吉", "谷鸡泰", "雪伦", "雷光",
            "燃谷", "幽若离", "大仓", "吧咕哒", "卡拉肖克·玲", "卡拉肖克·潘", "狄秋", "远浪", "离离艾", "白落提",
            "丰和", "桓泽金", "广秀", "英宋", "海问香", "脉兽秀秀", "玛朵布莎·爪云", "玛朵布莎·辞", "梅龙尼卡·嘉", "梅龙尼卡·蹄",
            "雷光", "紫霜", "鹿满", "狼勇", "熊枭", "秋落木", "离离茶", "花芫", "叶鹏", "泱",
            "勒克米罗·修", "梅龙尼卡·诚", "阿赫流瑟·兰", "迷麟", "卡拉肖克·飞", "卡拉肖克·雄", "玛朵布莎·小云", "万两", "玛朵布莎·白", "天宠"
            // "","","","","", "" , "","","",""
            // "","","","","", "" , "","","",""
    );

    private static final List<String> NATION_LIST = ArrayUtils.asUnmodifiableList(
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
        return PROVINCE_LIST.get(random.nextInt(Integer.MAX_VALUE) % PROVINCE_LIST.size());
    }

    protected final String randomRegion() {
        return randomRegion(ThreadLocalRandom.current());
    }

    protected final String randomRegion(Random random) {
        return REGION_LIST.get(random.nextInt(Integer.MAX_VALUE) % REGION_LIST.size());
    }

    protected final String randomCity() {
        return randomCity(ThreadLocalRandom.current());
    }

    protected final String randomCity(Random random) {
        return CITY_LIST.get(random.nextInt(Integer.MAX_VALUE) % CITY_LIST.size());
    }

    protected final String randomPerson() {
        return randomPerson(ThreadLocalRandom.current());
    }

    protected final String randomPerson(Random random) {
        return PERSON_LIST.get(random.nextInt(Integer.MAX_VALUE) % PERSON_LIST.size());
    }

    protected final String randomNation() {
        return randomNation(ThreadLocalRandom.current());
    }

    protected final String randomNation(Random random) {
        return NATION_LIST.get(random.nextInt(Integer.MAX_VALUE) % NATION_LIST.size());
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
        final List<ChinaRegion<?>> list = new ArrayList<>();
        ChinaRegion<?> c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaRegion<>()
                    .setId(Math.abs(random.nextLong()))
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
        final List<ChinaCity> list = new ArrayList<>();
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
        final List<ChinaProvince> list = new ArrayList<>();
        ChinaProvince c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaProvince()
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

            list.add(c);
        }
        return list;
    }


}
