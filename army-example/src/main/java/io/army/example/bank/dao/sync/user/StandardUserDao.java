package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import io.army.example.common.Pair;
import io.army.meta.ChildTableMeta;
import io.army.sync.SyncSession;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository("bankSyncStandardUserDao")
@Profile({BaseService.SYNC, BeanUtils.STANDARD})
public class StandardUserDao extends BankSyncBaseDao implements BankUserDao {


    @Override
    public boolean isExists(String certificateNo, CertificateType certificateType, BankUserType userType) {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankUser_.id)
                .from(Certificate_.T, "c")
                .join(BankUser_.T, "u").on(Certificate_.id.equal(BankUser_.certificateId))
                .where(Certificate_.certificateNo.equal(certificateNo))
                .and(Certificate_.certificateType.equalLiteral(certificateType))
                .and(BankUser_.userType.equalLiteral(userType))
                .limit(1)
                .asQuery();
        return this.sessionContext.currentSession().queryOne(stmt, Long.class) != null;
    }

    @Override
    public <T extends Certificate<T>> T getCertificate(String certificateNo
            , CertificateType certificateType, Class<T> domainType) {
        final SyncSession session;
        session = this.sessionContext.currentSession();

        final Select stmt;
        if (Certificate.class.equals(domainType)) {
            stmt = SQLs.query()
                    .select(SQLs.group(Certificate_.T, "t"))
                    .from(Certificate_.T, "t")
                    .where(Certificate_.certificateNo.equal(certificateNo))
                    .and(Certificate_.certificateType.equalLiteral(certificateType))
                    .asQuery();
        } else {
            final ChildTableMeta<T> child = (ChildTableMeta<T>) session.tableMeta(domainType);
            stmt = SQLs.query()
                    .select(SQLs.childGroup(child, "c", "p"))
                    .from(child, "c")
                    .join(Certificate_.T, "p").on(child.id().equal(Certificate_.id))
                    .where(Certificate_.certificateNo.equal(certificateNo))
                    .and(Certificate_.certificateType.equalLiteral(certificateType))
                    .asQuery();
        }
        return session.queryOne(stmt, domainType);
    }

    @Override
    public Pair<Long, BankUserType> getUserPair(String userNo) {
        final Select stmt;
        stmt = SQLs.query()
                .select(BankUser_.id, BankUser_.userType)
                .from(BankUser_.T, "t")
                .where(BankUser_.userNo.equal(userNo))
                .asQuery();
        return selectAsPair(this.sessionContext.currentSession(), stmt);
    }


}
