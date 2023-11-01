package io.army.example.bank.dao.sync.region;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;

@Repository("bankSyncMySQL57RegionDao")
@Profile({BaseService.SYNC, BeanUtils.MY_SQL57})
public class MySQL57RegionDao extends BankSyncBaseDao implements BankRegionDao {

    @Override
    public List<Map<String, Object>> findAllCity() {
        final Select stmt;
        stmt = MySQLs.query()
                .select(SQLs.field("p_of_city", ChinaRegion_.name), ChinaCity_.mayorName, SQLs.field("province", ChinaRegion_.name))
                .from(ChinaCity_.T, AS, "city")
                .join(ChinaRegion_.T, AS, "p_of_city")
                .on(ChinaCity_.id.equal(SQLs.field("p_of_city", ChinaRegion_.id)))
                .join(ChinaRegion_.T, AS, "province")
                .on(SQLs.field("p_of_city", ChinaRegion_.parentId)::equal, SQLs.field("province", ChinaRegion_.id))
                .asQuery();
        return this.sessionContext.currentSession().queryObjectList(stmt, HashMap::new);
    }

    @Override
    public void batchSaveProvincialCapital(List<ChinaCity> domainList) {

        final Supplier<Expression> provinceIdSubQuery;
        provinceIdSubQuery = () -> MySQLs.scalarSubQuery()
                .select(ChinaProvince_.id)
                .from(ChinaProvince_.T, AS, "p")
                .join(ChinaRegion_.T, AS, "r").on(ChinaProvince_.id.equal(ChinaRegion_.id))
                .where(ChinaProvince_.provincialCapital::equal, SQLs::literal, ChinaRegion_.NAME)
                .asQuery();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.parentId))
                .values(domainList)
                .asInsert()
                .child()
                .insertInto(ChinaCity_.T)
                .values(domainList)
                .asInsert();
        this.sessionContext.currentSession().update(stmt);
    }

    @Override
    public Long getRegionId(String regionName, RegionType regionType) {
        final Select stmt;
        stmt = MySQLs.query()
                .select(ChinaRegion_.id)
                .from(ChinaRegion_.T, AS, "t")
                .where(ChinaRegion_.name.equal(SQLs::param, regionName))
                .and(ChinaRegion_.regionType::equal, SQLs::literal, regionType)
                .asQuery();
        return this.sessionContext.currentSession().queryOne(stmt, Long.class);
    }


}
