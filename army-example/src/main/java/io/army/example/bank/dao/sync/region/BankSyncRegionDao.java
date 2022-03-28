package io.army.example.bank.dao.sync.region;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.user.*;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Repository("bankSyncRegionDao")
public class BankSyncRegionDao extends BankSyncBaseDao implements BankRegionDao {

    @Override
    public List<Map<String, Object>> findAllCity() {
        final Select stmt;
        stmt = SQLs.query()
                .select(list -> {
                    list.add(SQLs.field("p_of_city", ChinaRegion_.name));
                    list.add(ChinaCity_.mayorName);
                    list.add(SQLs.field("province", ChinaRegion_.name).as("province"));
                })
                .from(ChinaCity_.T, "city")
                .join(ChinaRegion_.T, "p_of_city")
                .on(ChinaCity_.id.equal(SQLs.field("p_of_city", ChinaRegion_.id)))
                .join(ChinaRegion_.T, "province")
                .on(SQLs.field("p_of_city", ChinaRegion_.parentId).equal(SQLs.field("province", ChinaRegion_.id)))
                .asQuery();
        return this.sessionContext.currentSession().selectAsMap(stmt);
    }


    @Override
    public void batchSaveProvincialCapital(final List<ChinaCity> domainList) {

        final Supplier<Expression> provinceIdSubQuery;
        provinceIdSubQuery = () -> SQLs.scalarSubQuery()
                .select(ChinaProvince_.id)
                .from(ChinaProvince_.T, "p")
                .join(ChinaRegion_.T, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                .where(ChinaProvince_.provincialCapital.equalNamed(ChinaRegion_.NAME))
                .asQuery();

        final Insert stmt;
        stmt = SQLs.valueInsert(ChinaCity_.T)
                .insertInto(Arrays.asList(ChinaCity_.name, ChinaCity_.parentId))
                .setExp(ChinaCity_.parentId, provinceIdSubQuery)
                .values(domainList)
                .asInsert();
        this.sessionContext.currentSession().update(stmt);
    }

    @Override
    public Long getRegionId(final String regionName, final RegionType regionType) {
        final Select stmt;
        stmt = SQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, "t")
                .where(ChinaRegion_.name.equalLiteral(regionName))
                .and(ChinaRegion_.regionType.equalLiteral(regionType))
                .asQuery();
        return this.sessionContext.currentSession().selectOne(stmt, Long.class);
    }


}
