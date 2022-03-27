package io.army.example.bank.service.sync.region;

import io.army.example.bank.service.reactive.region.BankRegionService;
import io.army.example.bank.service.sync.BankBaseServiceAdapter;
import io.army.example.common.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;

@Component("bankRegionServiceAdapter")
@Profile(BaseService.SYNC)
public class BankRegionServiceAdapter extends BankBaseServiceAdapter implements BankRegionService {

    private BankSyncRegionService regionService;


    @Override
    public Flux<Map<String, Object>> createRegionIfNotExists() {
        return Flux.defer(() -> Flux.fromIterable(this.regionService.createRegionIfNotExists()));
    }


    @Autowired
    public void setRegionService(@Qualifier("bankSyncRegionService") BankSyncRegionService regionService) {
        this.regionService = regionService;
    }


}
