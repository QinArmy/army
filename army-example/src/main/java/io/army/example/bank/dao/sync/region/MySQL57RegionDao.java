package io.army.example.bank.dao.sync.region;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Repository("bankSyncMySQL57RegionDao")
@Profile({BaseService.SYNC, BeanUtils.MY_SQL57})
public class MySQL57RegionDao extends BankSyncBaseDao implements BankRegionDao {

    @Override
    public List<Map<String, Object>> findAllCity() {
        final Select stmt;
        stmt = MySQLs.query()
                .select(consumer -> {
                    consumer.accept(SQLs.field("p_of_city", ChinaRegion_.name));
                    consumer.accept(ChinaCity_.mayorName);
                    consumer.accept(SQLs.field("province", ChinaRegion_.name).as("province"));
                })
                .from(ChinaCity_.T, "city")
                .join(ChinaRegion_.T, "p_of_city")
                .on(ChinaCity_.id.equal(SQLs.field("p_of_city", ChinaRegion_.id)))
                .join(ChinaRegion_.T, "province")
                .on(SQLs.field("p_of_city", ChinaRegion_.parentId).equal(SQLs.field("province", ChinaRegion_.id)))
                .asQuery();
        return this.sessionContext.currentSession().queryAsMap(stmt);
    }

    @Override
    public void batchSaveProvincialCapital(List<ChinaCity> domainList) {

        final Supplier<Expression> provinceIdSubQuery;
        provinceIdSubQuery = () -> MySQLs.scalarSubQuery()
                .select(ChinaProvince_.id)
                .from(ChinaProvince_.T, "p")
                .join(ChinaRegion_.T, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                .where(ChinaProvince_.provincialCapital.equalNamed(ChinaRegion_.NAME))
                .asQuery();

        final Insert stmt;
        stmt = SQLs.valueInsert()
                .insertInto(ChinaCity_.T)
                .leftParen(ChinaCity_.name).comma(ChinaCity_.parentId)
                .rightParen()
                .commonExp(ChinaCity_.parentId, provinceIdSubQuery)
                .values(domainList)
                .asInsert();
        this.sessionContext.currentSession().update(stmt);
    }

    @Override
    public Long getRegionId(String regionName, RegionType regionType) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, "t")
                .where(ChinaRegion_.name.equalLiteral(regionName))
                .and(ChinaRegion_.regionType.equalLiteral(regionType))
                .asQuery();
        return this.sessionContext.currentSession().queryOne(stmt, Long.class);
    }


}
