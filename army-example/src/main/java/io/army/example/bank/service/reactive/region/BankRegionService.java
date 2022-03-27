package io.army.example.bank.service.reactive.region;

import io.army.example.common.BaseService;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface BankRegionService extends BaseService {

    Flux<Map<String, Object>> createRegionIfNotExists();
}
