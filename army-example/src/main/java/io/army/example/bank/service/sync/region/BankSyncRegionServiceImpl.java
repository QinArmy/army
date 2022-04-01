package io.army.example.bank.service.sync.region;

import io.army.example.bank.dao.sync.region.BankRegionDao;
import io.army.example.bank.domain.user.ChinaCity;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("bankSyncRegionService")
@Profile(BaseService.SYNC)
public class BankSyncRegionServiceImpl extends BankSyncBaseService implements BankSyncRegionService {

    private BankRegionDao regionDao;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.regionDao = BeanUtils.getDao("bankSync%sRegionDao", BankRegionDao.class, this.applicationContext);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public List<Map<String, Object>> createRegionIfNotExists() {
        final BankRegionDao regionDao = this.regionDao;
        List<Map<String, Object>> list;
        list = regionDao.findAllCity();
        if (list.size() == 0) {
            regionDao.batchSave(createProvinces());
            regionDao.batchSaveProvincialCapital(createProvincialCapitals());

            list = regionDao.findAllCity();
        }
        return list;
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED, readOnly = true)
    @Override
    public Long getRegionId(String regionName, RegionType regionType) {
        return this.regionDao.getRegionId(regionName, regionType);
    }


    private List<ChinaProvince> createProvinces() {
        final List<ChinaProvince> list = new ArrayList<>(34);
        ChinaProvince p;

        p = new ChinaProvince()
                .setName("北京市")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("北京市")
                .setRegionGdp(new BigDecimal("4026960000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("天津市")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("天津市")
                .setRegionGdp(new BigDecimal("1569505000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("河北")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("石家庄市")
                .setRegionGdp(new BigDecimal("4039130000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("山西")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("太原市")
                .setRegionGdp(new BigDecimal("2259016000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("内蒙古自治区")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("呼和浩特市")
                .setRegionGdp(new BigDecimal("2051420000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("辽宁")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("沈阳市")
                .setRegionGdp(new BigDecimal("2758410000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("吉林")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("长春市")
                .setRegionGdp(new BigDecimal("1323552000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("黑龙江")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("哈尔滨市")
                .setRegionGdp(new BigDecimal("1487920000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("上海市")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("上海市")
                .setRegionGdp(new BigDecimal("4321485000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("江苏")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("南京市")
                .setRegionGdp(new BigDecimal("11636420000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("浙江")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("杭州市")
                .setRegionGdp(new BigDecimal("7351600000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("安徽")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("合肥市")
                .setRegionGdp(new BigDecimal("4295920000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("福建")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("福州市")
                .setRegionGdp(new BigDecimal("4881000000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("江西")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("南昌市")
                .setRegionGdp(new BigDecimal("2961970000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("山东")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("济南市")
                .setRegionGdp(new BigDecimal("8309590000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("台湾")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("台北市")
                .setRegionGdp(new BigDecimal("611451000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("河南")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("郑州市")
                .setRegionGdp(new BigDecimal("5888741000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("湖北")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("武汉市")
                .setRegionGdp(new BigDecimal("5001294000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("湖南")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("长沙市")
                .setRegionGdp(new BigDecimal("4606309000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("广东")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("广州市")
                .setRegionGdp(new BigDecimal("12436967000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("广西")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("南宁市")
                .setRegionGdp(new BigDecimal("2474086000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("海南")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("海口市")
                .setRegionGdp(new BigDecimal("647520000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("香港特别行政区")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("香港市")
                .setRegionGdp(new BigDecimal("366030000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("澳门特别行政区")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("澳门市")
                .setRegionGdp(new BigDecimal("33944720000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("重庆市")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("重庆市")
                .setRegionGdp(new BigDecimal("2789402000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("四川")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("成都市")
                .setRegionGdp(new BigDecimal("5385079000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("贵州")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("贵阳市")
                .setRegionGdp(new BigDecimal("1782656000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("云南")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("昆明市")
                .setRegionGdp(new BigDecimal("2714676000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("西藏自治区")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("拉萨市")
                .setRegionGdp(new BigDecimal("208017000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("陕西")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("西安市")
                .setRegionGdp(new BigDecimal("2980098000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("甘肃")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("兰州市")
                .setRegionGdp(new BigDecimal("1024330000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("青海")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("西宁市")
                .setRegionGdp(new BigDecimal("334663000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("宁夏")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("银川市")
                .setRegionGdp(new BigDecimal("392055000000"));
        list.add(p);

        p = new ChinaProvince()
                .setName("新疆维吾尔自治区")
                .setParentId(0L)
                .setGovernor("")
                .setProvincialCapital("乌鲁木齐市")
                .setRegionGdp(new BigDecimal("1598365000000"));
        list.add(p);
        return list;
    }

    private List<ChinaCity> createProvincialCapitals() {
        final List<ChinaCity> list = new ArrayList<>(34);
        ChinaCity c;

        c = new ChinaCity()
                .setName("北京市");
        list.add(c);

        c = new ChinaCity()
                .setName("天津市");
        list.add(c);

        c = new ChinaCity()
                .setName("石家庄市");
        list.add(c);

        c = new ChinaCity()
                .setName("太原市");
        list.add(c);

        c = new ChinaCity()
                .setName("呼和浩特市");
        list.add(c);

        c = new ChinaCity()
                .setName("沈阳市");
        list.add(c);

        c = new ChinaCity()
                .setName("长春市");
        list.add(c);

        c = new ChinaCity()
                .setName("哈尔滨市");
        list.add(c);

        c = new ChinaCity()
                .setName("上海市");
        list.add(c);

        c = new ChinaCity()
                .setName("南京市");
        list.add(c);

        c = new ChinaCity()
                .setName("杭州市");
        list.add(c);

        c = new ChinaCity()
                .setName("合肥市");
        list.add(c);

        c = new ChinaCity()
                .setName("福州市");
        list.add(c);

        c = new ChinaCity()
                .setName("南昌市");
        list.add(c);

        c = new ChinaCity()
                .setName("济南市");
        list.add(c);

        c = new ChinaCity()
                .setName("台北市");
        list.add(c);

        c = new ChinaCity()
                .setName("郑州市");
        list.add(c);

        c = new ChinaCity()
                .setName("武汉市");
        list.add(c);

        c = new ChinaCity()
                .setName("长沙市");
        list.add(c);

        c = new ChinaCity()
                .setName("广州市");
        list.add(c);

        c = new ChinaCity()
                .setName("南宁市");
        list.add(c);

        c = new ChinaCity()
                .setName("海口市");
        list.add(c);

        c = new ChinaCity()
                .setName("香港市");
        list.add(c);

        c = new ChinaCity()
                .setName("澳门市");
        list.add(c);

        c = new ChinaCity()
                .setName("重庆市");
        list.add(c);

        c = new ChinaCity()
                .setName("成都市");
        list.add(c);

        c = new ChinaCity()
                .setName("贵阳市");
        list.add(c);

        c = new ChinaCity()
                .setName("昆明市");
        list.add(c);

        c = new ChinaCity()
                .setName("拉萨市");
        list.add(c);

        c = new ChinaCity()
                .setName("西安市");
        list.add(c);

        c = new ChinaCity()
                .setName("兰州市");
        list.add(c);

        c = new ChinaCity()
                .setName("西宁市");
        list.add(c);

        c = new ChinaCity()
                .setName("银川市");
        list.add(c);

        c = new ChinaCity()
                .setName("乌鲁木齐市");
        list.add(c);
        return list;
    }


}
