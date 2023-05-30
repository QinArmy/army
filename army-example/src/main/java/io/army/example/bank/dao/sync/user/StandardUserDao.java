package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import io.army.example.common.Pair;
import io.army.meta.ComplexTableMeta;
import io.army.sync.SyncSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Repository("bankSyncStandardUserDao")
@Profile({BaseService.SYNC, BeanUtils.STANDARD})
public class StandardUserDao extends BankSyncBaseDao implements BankUserDao {


    @Override
    public boolean isExists(String certificateNo, CertificateType certificateType, BankUserType userType) {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankUser_.id)
                .from(Certificate_.T, AS, "c")
                .join(BankUser_.T, AS, "u").on(Certificate_.id::equal, BankUser_.certificateId)
                .where(Certificate_.certificateNo.equal(SQLs::param, certificateNo))
                .and(Certificate_.certificateType::equal, SQLs::literal, certificateType)
                .and(BankUser_.userType::equal, SQLs::literal, userType)
                .limit(SQLs::literal,1)
                .asQuery();
        return this.sessionContext.currentSession().queryOne(stmt, Long.class) != null;
    }

    @Override
    public <P, T extends Certificate<T>> T getCertificate(String certificateNo
            , CertificateType certificateType, Class<T> domainType) {
        final SyncSession session;
        session = this.sessionContext.currentSession();

        final Select stmt;
        if (Certificate.class.equals(domainType)) {
            stmt = SQLs.query()
                    .select("t", PERIOD, Certificate_.T)
                    .from(Certificate_.T, AS, "t")
                    .where(Certificate_.certificateNo.equal(SQLs::param, certificateNo))
                    .and(Certificate_.certificateType::equal, SQLs::literal, certificateType)
                    .asQuery();
        } else {
            final ComplexTableMeta<P, T> child = (ComplexTableMeta<P, T>) session.tableMeta(domainType);
            stmt = SQLs.query()
                    .select("p", PERIOD, child.parentMeta(), "c", PERIOD, child)
                    .from(child, AS, "c")
                    .join(Certificate_.T, AS, "p").on(child.id()::equal, Certificate_.id)
                    .where(Certificate_.certificateNo.equal(SQLs::param, certificateNo))
                    .and(Certificate_.certificateType::equal, SQLs::literal, certificateType)
                    .asQuery();
        }
        return session.queryOne(stmt, domainType);
    }

    @Override
    public Pair<Long, BankUserType> getUserPair(String userNo) {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankUser_.id, BankUser_.userType)
                .from(BankUser_.T, AS, "t")
                .where(BankUser_.userNo.equal(SQLs::param, userNo))
                .asQuery();
        return selectAsPair(this.sessionContext.currentSession(), stmt);
    }


}
