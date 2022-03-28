package io.army.example.bank.dao.sync.region;

import io.army.example.bank.domain.user.ChinaCity;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.common.SyncBaseDao;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface BankRegionDao extends SyncBaseDao {

    List<Map<String, Object>> findAllCity();

    void batchSaveProvincialCapital(List<ChinaCity> domainList);

    @Nullable
    Long getRegionId(String regionName, RegionType regionType);

}
