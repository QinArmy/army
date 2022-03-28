package io.army.example.bank.service.sync.region;

import io.army.example.bank.domain.user.RegionType;
import io.army.example.common.SyncBaseService;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

public interface BankSyncRegionService extends SyncBaseService {

    List<Map<String, Object>> createRegionIfNotExists();

    @Nullable
    Long getRegionId(String regionName, RegionType regionType);

}
