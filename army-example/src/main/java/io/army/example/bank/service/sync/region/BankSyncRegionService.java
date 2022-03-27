package io.army.example.bank.service.sync.region;

import io.army.example.common.SyncBaseService;

import java.util.List;
import java.util.Map;

public interface BankSyncRegionService extends SyncBaseService {

    List<Map<String, Object>> createRegionIfNotExists();

}
